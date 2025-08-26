package me.seetch;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.Effect;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.NamespacedKey;

/**
 * Utility class for generating Minecraft translation keys
 */
@UtilityClass
public class TranslationKeyGenerator {

    /**
     * Generate translation key for material
     *
     * @param material Minecraft material
     * @return translation key
     */
    public String getMaterialKey(Material material) {
        if (material.isBlock()) {
            return "block.minecraft." + material.name().toLowerCase();
        } else {
            return "item.minecraft." + material.name().toLowerCase();
        }
    }

    /**
     * Generate translation key for effect
     *
     * @param effect Minecraft effect
     * @return translation key
     */
    public String getEffectKey(Effect effect) {
        return "effect.minecraft." + effect.name().toLowerCase();
    }

    /**
     * Generate translation key for enchantment
     *
     * @param enchantment Minecraft enchantment
     * @return translation key
     */
    public String getEnchantmentKey(Enchantment enchantment) {
        NamespacedKey key = enchantment.getKey();
        if (key.getNamespace().equals("minecraft")) {
            return "enchantment.minecraft." + key.getKey();
        }
        return "enchantment." + key.getNamespace() + "." + key.getKey();
    }

    /**
     * Generate translation key for entity type
     *
     * @param entityType Minecraft entity type
     * @return translation key
     */
    public String getEntityKey(EntityType entityType) {
        return "entity.minecraft." + entityType.name().toLowerCase();
    }

    /**
     * Generate translation key for ItemStack
     *
     * @param itemStack ItemStack
     * @return translation key
     */
    public String getItemStackKey(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return "block.minecraft.air";
        }

        Material material = itemStack.getType();
        if (material.isBlock()) {
            return "block.minecraft." + material.name().toLowerCase();
        } else {
            return "item.minecraft." + material.name().toLowerCase();
        }
    }

    /**
     * Generate translation key for custom text
     *
     * @param key custom key
     * @return formatted translation key
     */
    public String getCustomKey(String key) {
        return "mlang." + key.toLowerCase().replace(" ", "_").replace("-", "_");
    }
}
