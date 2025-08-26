package me.seetch;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.Material;
import org.bukkit.Effect;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Main class of MLang library for working with Minecraft localization
 * <p>
 * Usage example:
 * <p>
 * // Initialization
 * MLang lang = MLang.getInstance(plugin);
 * lang.setDefaultLanguage("ru_ru");
 * <p>
 * // Loading language
 * lang.loadDefaultLanguageAsync().thenAccept(success -> {
 * if (success) {
 * String translation = lang.getMaterialTranslation(Material.STONE);
 * System.out.println(translation); // "Камень"
 * }
 * });
 */
@Log
@Getter
public class MLang {
    private static MLang instance;
    private final JavaPlugin plugin;
    private final Gson gson;
    private final Map<String, JsonObject> loadedLanguages;
    private String defaultLanguage;
    private String defaultVersion;

    private MLang(JavaPlugin plugin) {
        this.plugin = plugin;
        this.gson = new Gson();
        this.loadedLanguages = new HashMap<>();
        this.defaultLanguage = "en_us";
        this.defaultVersion = detectMinecraftVersion();

        // Create languages directory
        File langDir = new File(plugin.getDataFolder(), "languages");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }
    }

    /**
     * Get library instance (Singleton)
     *
     * @param plugin your JavaPlugin
     * @return MLang instance
     */
    public static synchronized MLang getInstance(JavaPlugin plugin) {
        if (instance == null) {
            instance = new MLang(plugin);
        }
        return instance;
    }

    /**
     * Set default language
     *
     * @param languageCode language code (e.g., "en_us", "ru_ru")
     */
    public void setDefaultLanguage(String languageCode) {
        this.defaultLanguage = languageCode.toLowerCase();
    }

    /**
     * Set default Minecraft version
     *
     * @param version Minecraft version (e.g., "1.20.4")
     */
    public void setDefaultVersion(String version) {
        this.defaultVersion = version;
    }

    /**
     * Asynchronously load language file
     *
     * @param languageCode language code
     * @param version      Minecraft version
     * @return CompletableFuture<Boolean> loading result
     */
    public CompletableFuture<Boolean> loadLanguageAsync(String languageCode, String version) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return loadLanguage(languageCode, version);
            } catch (Exception e) {
                log.severe("Failed to load language " + languageCode + ": " + e.getMessage());
                return false;
            }
        });
    }

    /**
     * Synchronously load language file
     *
     * @param languageCode language code
     * @param version      Minecraft version
     * @return true if successfully loaded
     */
    public boolean loadLanguage(String languageCode, String version) {
        languageCode = languageCode.toLowerCase();
        version = version.toLowerCase();

        String fileName = languageCode + ".json";
        File langDir = new File(plugin.getDataFolder(), "languages");
        File langFile = new File(langDir, fileName);

        // If file already exists and loaded, return true
        if (loadedLanguages.containsKey(languageCode)) {
            return true;
        }

        // Download file if it doesn't exist
        if (!langFile.exists()) {
            if (!downloadLanguageFile(languageCode, version, langFile)) {
                return false;
            }
        }

        // Load JSON into memory
        try (FileReader reader = new FileReader(langFile)) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            if (jsonObject != null) {
                loadedLanguages.put(languageCode, jsonObject);
                log.info("Successfully loaded language: " + languageCode);
                return true;
            }
        } catch (Exception e) {
            log.severe("Failed to parse language file: " + languageCode + " - " + e.getMessage());
        }

        return false;
    }

    /**
     * Load default language asynchronously
     *
     * @return CompletableFuture<Boolean> loading result
     */
    public CompletableFuture<Boolean> loadDefaultLanguageAsync() {
        return loadLanguageAsync(defaultLanguage, defaultVersion);
    }

    /**
     * Download language file from GitHub
     */
    private boolean downloadLanguageFile(String languageCode, String version, File saveTo) {
        String baseUrl = "https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/";
        String fileUrl = baseUrl + version + "/assets/minecraft/lang/" + languageCode + ".json";

        try {
            log.info("Downloading language file: " + languageCode + " (" + version + ")");

            try (BufferedInputStream in = new BufferedInputStream(new URL(fileUrl).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(saveTo)) {

                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            }

            log.info("Successfully downloaded language file: " + languageCode);
            return true;
        } catch (Exception e) {
            log.severe("Failed to download language file: " + languageCode + " - " + e.getMessage());
            log.severe("URL: " + fileUrl);
            return false;
        }
    }

    /**
     * Get translation by key
     *
     * @param languageCode language code
     * @param key          translation key
     * @return translation or key if not found
     */
    public String getTranslation(String languageCode, String key) {
        JsonObject langJson = loadedLanguages.get(languageCode.toLowerCase());
        if (langJson == null || !langJson.has(key)) {
            // Try to use default language
            if (!languageCode.equals(defaultLanguage)) {
                return getTranslation(defaultLanguage, key);
            }
            return key; // Return key if translation not found
        }
        return langJson.get(key).getAsString();
    }

    /**
     * Get translation using default language
     *
     * @param key translation key
     * @return translation
     */
    public String getTranslation(String key) {
        return getTranslation(defaultLanguage, key);
    }

    /**
     * Get translation for material
     *
     * @param languageCode language code
     * @param material     Minecraft material
     * @return material translation
     */
    public String getMaterialTranslation(String languageCode, Material material) {
        String key = TranslationKeyGenerator.getMaterialKey(material);
        return getTranslation(languageCode, key);
    }

    /**
     * Get translation for material with default language
     *
     * @param material Minecraft material
     * @return material translation
     */
    public String getMaterialTranslation(Material material) {
        return getMaterialTranslation(defaultLanguage, material);
    }

    /**
     * Get translation for effect
     *
     * @param languageCode language code
     * @param effect       Minecraft effect
     * @return effect translation
     */
    public String getEffectTranslation(String languageCode, Effect effect) {
        String key = TranslationKeyGenerator.getEffectKey(effect);
        return getTranslation(languageCode, key);
    }

    /**
     * Get translation for effect with default language
     *
     * @param effect Minecraft effect
     * @return effect translation
     */
    public String getEffectTranslation(Effect effect) {
        return getEffectTranslation(defaultLanguage, effect);
    }

    /**
     * Get translation for enchantment
     *
     * @param languageCode language code
     * @param enchantment  Minecraft enchantment
     * @return enchantment translation
     */
    public String getEnchantmentTranslation(String languageCode, Enchantment enchantment) {
        String key = TranslationKeyGenerator.getEnchantmentKey(enchantment);
        return getTranslation(languageCode, key);
    }

    /**
     * Get translation for enchantment with default language
     *
     * @param enchantment Minecraft enchantment
     * @return enchantment translation
     */
    public String getEnchantmentTranslation(Enchantment enchantment) {
        return getEnchantmentTranslation(defaultLanguage, enchantment);
    }

    /**
     * Get translation for entity type
     *
     * @param languageCode language code
     * @param entityType   Minecraft entity type
     * @return entity type translation
     */
    public String getEntityTranslation(String languageCode, EntityType entityType) {
        String key = TranslationKeyGenerator.getEntityKey(entityType);
        return getTranslation(languageCode, key);
    }

    /**
     * Get translation for entity type with default language
     *
     * @param entityType Minecraft entity type
     * @return entity type translation
     */
    public String getEntityTranslation(EntityType entityType) {
        return getEntityTranslation(defaultLanguage, entityType);
    }

    /**
     * Get translation for ItemStack
     *
     * @param languageCode language code
     * @param itemStack    ItemStack
     * @return ItemStack translation
     */
    public String getItemStackTranslation(String languageCode, ItemStack itemStack) {
        String key = TranslationKeyGenerator.getItemStackKey(itemStack);
        return getTranslation(languageCode, key);
    }

    /**
     * Get translation for ItemStack with default language
     *
     * @param itemStack ItemStack
     * @return ItemStack translation
     */
    public String getItemStackTranslation(ItemStack itemStack) {
        return getItemStackTranslation(defaultLanguage, itemStack);
    }

    /**
     * Check if language is loaded
     *
     * @param languageCode language code
     * @return true if language is loaded
     */
    public boolean isLanguageLoaded(String languageCode) {
        return loadedLanguages.containsKey(languageCode.toLowerCase());
    }

    /**
     * Get all loaded languages
     *
     * @return array of loaded language codes
     */
    public String[] getLoadedLanguages() {
        return loadedLanguages.keySet().toArray(new String[0]);
    }

    /**
     * Auto-detect Minecraft version
     */
    private String detectMinecraftVersion() {
        try {
            String version = org.bukkit.Bukkit.getBukkitVersion();
            if (version.contains("-")) {
                return version.split("-")[0];
            }
            return version;
        } catch (Exception e) {
            return "1.20.4"; // Default version
        }
    }
}
