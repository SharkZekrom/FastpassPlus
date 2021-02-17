package fr.shark_zekrom.Fastpass;

import com.google.common.base.Enums;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public enum XMaterial {

    DARK_OAK_SIGN("SIGN_POST", "SIGN"),
    DARK_OAK_WALL_SIGN("WALL_SIGN"),

    SPRUCE_SIGN("SIGN_POST", "SIGN"),
    SPRUCE_WALL_SIGN("WALL_SIGN"),

    JUNGLE_SIGN("SIGN_POST", "SIGN"),
    JUNGLE_WALL_SIGN("WALL_SIGN"),

    OAK_SIGN("SIGN_POST", "SIGN"),
    OAK_WALL_SIGN("SIGN_POST", "SIGN"),

    ACACIA_SIGN("SIGN_POST", "SIGN"),
    ACACIA_WALL_SIGN("WALL_SIGN"),

    BIRCH_SIGN("SIGN_POST", "SIGN"),
    BIRCH_WALL_SIGN("WALL_SIGN"),

    CRIMSON_SIGN(0, 16, "SIGN_POST"),
    CRIMSON_WALL_SIGN(0, 16, "WALL_SIGN"),

    WARPED_SIGN(0, 16, "SIGN_POST"),
    WARPED_WALL_SIGN(0, 16, "WALL_SIGN");





    private static final Map<String, XMaterial> NAMES = new HashMap<>();


    private static final Cache<String, XMaterial> NAME_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build();

    private static final LoadingCache<String, Pattern> CACHED_REGEX = CacheBuilder.newBuilder()
            .expireAfterAccess(3, TimeUnit.HOURS)
            .build(new CacheLoader<String, Pattern>() {
                @Override
                public Pattern load(@Nonnull String str) {
                    try {
                        return Pattern.compile(str);
                    } catch (PatternSyntaxException ex) {
                        ex.printStackTrace();
                        return null;
                    }
                }
            });

    private static final byte MAX_DATA_VALUE = 120;

    private static final byte UNKNOWN_DATA_VALUE = -1;

    private static final short MAX_ID = 2267;





    private final byte data;

    private final byte version;

    @Nonnull
    private final String[] legacy;

    @Nullable
    private final Material material;

    XMaterial(int data, int version, @Nonnull String... legacy) {
        this.data = (byte) data;
        this.version = (byte) version;
        this.legacy = legacy;

        Material mat = null;
        if ((!Data.ISFLAT && this.isDuplicated()) || (mat = Material.getMaterial(this.name())) == null) {
            for (int i = legacy.length - 1; i >= 0; i--) {
                mat = Material.getMaterial(legacy[i]);
                if (mat != null) break;
            }
        }
        this.material = mat;
    }

    XMaterial(int data, @Nonnull String... legacy) {
        this(data, 0, legacy);
    }

    XMaterial(int version) {
        this(0, version);
    }

    XMaterial() {
        this(0, 0);
    }

    XMaterial(String... legacy) {
        this(0, 0, legacy);
    }


    public static boolean isNewVersion() {
        return Data.ISFLAT;
    }

    public static boolean isOneEight() {
        return !supports(9);
    }


    @Nonnull
    private static Optional<XMaterial> getIfPresent(@Nonnull String name) {
        return Optional.ofNullable(NAMES.get(name));
    }


    public static int getVersion() {
        return Data.VERSION;
    }






    @Nonnull
    protected static String format(@Nonnull String name) {
        int len = name.length();
        char[] chs = new char[len];
        int count = 0;
        boolean appendUnderline = false;

        for (int i = 0; i < len; i++) {
            char ch = name.charAt(i);

            if (!appendUnderline && count != 0 && (ch == '-' || ch == ' ' || ch == '_') && chs[count] != '_')
                appendUnderline = true;
            else {
                boolean number = false;
                // Old materials have numbers in them.
                if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (number = (ch >= '0' && ch <= '9'))) {
                    if (appendUnderline) {
                        chs[count++] = '_';
                        appendUnderline = false;
                    }

                    if (number) chs[count++] = ch;
                    else chs[count++] = (char) (ch & 0x5f);
                }
            }
        }

        return new String(chs, 0, count);
    }

    public static boolean supports(int version) {
        return Data.VERSION >= version;
    }

    @Nonnull
    public static String getMajorVersion(@Nonnull String version) {
        Validate.notEmpty(version, "Cannot get major Minecraft version from null or empty string");

        // getVersion()
        int index = version.lastIndexOf("MC:");
        if (index != -1) {
            version = version.substring(index + 4, version.length() - 1);
        } else if (version.endsWith("SNAPSHOT")) {
            // getBukkitVersion()
            index = version.indexOf('-');
            version = version.substring(0, index);
        }

        // 1.13.2, 1.14.4, etc...
        int lastDot = version.lastIndexOf('.');
        if (version.indexOf('.') != lastDot) version = version.substring(0, lastDot);

        return version;
    }

    public String[] getLegacy() {
        return this.legacy;
    }


    public boolean isOneOf(@Nullable Collection<String> materials) {
        if (materials == null || materials.isEmpty()) return false;
        String name = this.name();

        for (String comp : materials) {
            String checker = comp.toUpperCase(Locale.ENGLISH);
            if (checker.startsWith("CONTAINS:")) {
                comp = format(checker.substring(9));
                if (name.contains(comp)) return true;
                continue;
            }
            if (checker.startsWith("REGEX:")) {
                comp = comp.substring(6);
                Pattern pattern = CACHED_REGEX.getUnchecked(comp);
                if (pattern != null && pattern.matcher(name).matches()) return true;
                continue;
            }

            // Direct Object Equals

        }
        return false;
    }


    @Nonnull
    @SuppressWarnings("deprecation")
    public ItemStack setType(@Nonnull ItemStack item) {
        Objects.requireNonNull(item, "Cannot set material for null ItemStack");
        Material material = this.parseMaterial();
        Objects.requireNonNull(material, () -> "Unsupported material: " + this.name());

        item.setType(material);
        if (!Data.ISFLAT && material.getMaxDurability() <= 0) item.setDurability(this.data);
        return item;
    }


    private boolean anyMatchLegacy(@Nonnull String name) {
        for (int i = this.legacy.length - 1; i >= 0; i--) {
            if (name.equals(this.legacy[i])) return true;
        }
        return false;
    }


    @Override
    @Nonnull
    public String toString() {
        return WordUtils.capitalize(this.name().replace('_', ' ').toLowerCase(Locale.ENGLISH));
    }

    @SuppressWarnings("deprecation")
    public int getId() {
        if (this.data != 0 || this.version >= 13) return -1;
        Material material = this.parseMaterial();
        if (material == null) return -1;
        if (Data.ISFLAT && !material.isLegacy()) return -1;
        return material.getId();
    }

    @SuppressWarnings("deprecation")
    public byte getData() {
        return data;
    }

    @Nullable
    @SuppressWarnings("deprecation")
    public ItemStack parseItem() {
        Material material = this.parseMaterial();
        if (material == null) return null;
        return Data.ISFLAT ? new ItemStack(material) : new ItemStack(material, 1, this.data);
    }

    @Nullable
    public Material parseMaterial() {
        return this.material;
    }

    @SuppressWarnings("deprecation")
    public boolean isSimilar(@Nonnull ItemStack item) {
        Objects.requireNonNull(item, "Cannot compare with null ItemStack");
        if (item.getType() != this.parseMaterial()) return false;
        return Data.ISFLAT || item.getDurability() == this.data || item.getType().getMaxDurability() <= 0;
    }


    public boolean isSupported() {
        return this.material != null;
    }


    public byte getMaterialVersion() {
        return version;
    }

    private boolean isDuplicated() {
        switch (this.name()) {
            case "MELON":
            case "CARROT":
            case "POTATO":
            case "GRASS":
            case "BRICK":
            case "NETHER_BRICK":

                // Illegal Elements
                // Since both 1.12 and 1.13 have <type>_DOOR XMaterial will use it
                // for 1.12 to parse the material, but it needs <type>_DOOR_ITEM.
                // We'll trick XMaterial into thinking this needs to be parsed
                // using the old methods.
                // Some of these materials have their enum name added to the legacy list as well.
            case "DARK_OAK_DOOR":
            case "ACACIA_DOOR":
            case "BIRCH_DOOR":
            case "JUNGLE_DOOR":
            case "SPRUCE_DOOR":
            case "MAP":
            case "CAULDRON":
            case "BREWING_STAND":
            case "FLOWER_POT":
                return true;
            default:
                return false;
        }
    }

    private static final class Data {

        private static final int VERSION = Integer.parseInt(getMajorVersion(Bukkit.getVersion()).substring(2));

        private static final boolean ISFLAT = supports(13);
    }
}