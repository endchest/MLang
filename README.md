# 🌍 MLang - Minecraft Language Library

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Minecraft](https://img.shields.io/badge/minecraft-1.16--1.21.8-green.svg)]()
[![Java](https://img.shields.io/badge/java-8%2B-orange.svg)]()

**MLang** - Powerful and easy-to-use library for working with Minecraft localization. Allows you to easily get user-friendly translations for items, effects, enchantments, and other game elements.

## 🚀 Features

- ✅ **Automatic downloading** of language files from GitHub
- ✅ **Asynchronous operations** for non-blocking work
- ✅ **Caching** of loaded translations for high performance
- ✅ **Multi-language support** (en_us, ru_ru, es_es, de_de, etc.)
- ✅ **Fallback system** - automatic fallback to default language
- ✅ **Full coverage** - materials, effects, enchantments, entities, ItemStack
- ✅ **Simple API** - just a few lines of code to get started

## 📦 Installation

### Maven
```xml
<repository>
    <id>daycube-releases</id>
    <name>DAYCUBE</name>
    <url>https://repo.hy-pe.ru/releases</url>
</repository>

<dependency>
    <groupId>me.seetch</groupId>
    <artifactId>mlang</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle
```xml
maven {
    name "daycubeReleases"
    url "https://repo.hy-pe.ru/releases
}

implementation 'me.seetch:mlang:1.0.0'
```

### Manual
Download the JAR file from [Releases](https://github.com/dayqube/MLang/releases) and add it to your project.

## 🛠 Quick Start

```java
import me.seetch.MLang;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;

public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Initialize MLang
        MLang lang = MLang.getInstance(this);
        
        // Set default language
        lang.setDefaultLanguage("ru_ru");
        lang.setDefaultVersion("1.20.4");
        
        // Asynchronously load languages
        lang.loadDefaultLanguageAsync().thenAccept(success -> {
            if (success) {
                getLogger().info("Language loaded successfully!");
            }
        });
    }
    
    public void exampleUsage() {
        MLang lang = MLang.getInstance(this);
        
        // Get translation for material
        String translation = lang.getMaterialTranslation("ru_ru", Material.DIAMOND_SWORD);
        // Result: "Алмазный меч"
        
        // Using default language
        String defaultTranslation = lang.getMaterialTranslation(Material.STONE);
        // Result: "Камень" (if default language is ru_ru)
    }
}
```

## 📚 API Documentation

### Main Methods

#### Initialization
```java
MLang lang = MLang.getInstance(JavaPlugin plugin);
```

#### Configuration
```java
// Set default language
lang.setDefaultLanguage("en_us");

// Set Minecraft version
lang.setDefaultVersion("1.20.4");
```

#### Language Loading
```java
// Asynchronous loading
CompletableFuture<Boolean> future = lang.loadLanguageAsync("ru_ru", "1.20.4");

// Synchronous loading
boolean success = lang.loadLanguage("es_es", "1.20.4");

// Load default language
lang.loadDefaultLanguageAsync();
```

#### Getting Translations
```java
// Materials
String material = lang.getMaterialTranslation("ru_ru", Material.DIAMOND);

// Effects
String effect = lang.getEffectTranslation("en_us", Effect.SPEED);

// Enchantments
String enchantment = lang.getEnchantmentTranslation("de_de", Enchantment.SHARPNESS);

// Entity Types
String entity = lang.getEntityTranslation("fr_fr", EntityType.ZOMBIE);

// ItemStack
String item = lang.getItemStackTranslation("es_es", itemStack);

// Direct access to keys
String translation = lang.getTranslation("ru_ru", "block.minecraft.stone");
```

#### Utilities
```java
// Generate translation keys
String key = TranslationKeyGenerator.getMaterialKey(Material.STONE);

// Check loaded languages
boolean isLoaded = lang.isLanguageLoaded("ru_ru");

// List all loaded languages
String[] languages = lang.getLoadedLanguages();
```

## 🌐 Supported Languages

MLang supports all official Minecraft languages:
- en_us - English (United States)
- ru_ru - Русский
- es_es - Español (España)
- de_de - Deutsch (Deutschland)
- fr_fr - Français (France)
- zh_cn - 简体中文
- ja_jp - 日本語
- And many others...

## ⚙️ Configuration

MLang automatically creates a languages folder in your plugin directory to store downloaded files:

```
plugins/
└── YourPlugin/
└── languages/
├── en_us.json
├── ru_ru.json
├── es_es.json
└── ...
```

## 🤝 Integration with Other Plugins

```java
public class IntegrationExample {

    public String getItemDisplayName(ItemStack item, String playerLanguage) {
        MLang lang = MLang.getInstance(yourPlugin);
        
        // Get item name translation
        String name = lang.getItemStackTranslation(playerLanguage, item);
        
        // Add formatting for enchanted items
        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
            name = "§a" + name + " §7(Enchanted)";
        }
        
        return name;
    }
    
    public String getEntityName(EntityType type, String language) {
        MLang lang = MLang.getInstance(yourPlugin);
        return lang.getEntityTranslation(language, type);
    }
}
```

## 📈 Performance

- **Caching**: All loaded language files are stored in memory
- **Lazy loading**: Files are downloaded only when needed
- **Asynchronous**: Loading doesn't block the main server thread
- **Memory optimization**: Efficient resource usage

## 🐛 Error Handling

```java
lang.loadLanguageAsync("invalid_lang", "1.20.4")
.exceptionally(throwable -> {
    getLogger().warning("Failed to load language: " + throwable.getMessage());
    return false;
})
.thenAccept(success -> {
    if (!success) {
        getLogger().warning("Language was not loaded");
    }
});
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **[seetch](https://github.com/seetch)** - *Main Developer*

## 🙏 Acknowledgments

- [InventivetalentDev](https://github.com/InventivetalentDev) for minecraft-assets
- Bukkit/Spigot community for the excellent platform

## 📞 Support

If you have questions or suggestions, create an [Issue](https://github.com/dayqube/MLang/issues) on GitHub.