/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block.entity;

import com.handicraft.client.CommonMod;
import com.handicraft.client.item.CandySmeltingRecipe;
import com.handicraft.client.screen.NetheriteFurnaceScreenHandler;
import com.handicraft.client.util.HandiUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NetheriteFurnaceBlockEntity extends LockableContainerBlockEntity implements SidedInventory {
    private static final int[] INPUT_SLOTS = new int[]{1,2,3,4,5,6};
    private static final int[] OUTPUT_SLOTS = new int[]{7,8,9,10,11,12};

    private PropertyDelegate propertyDelegate;
    private int burnTime;
    private int fuelTime;
    private List<FurnaceModule> modules;
    private ItemStack fuel;


    public NetheriteFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(CommonMod.NETHERITE_FURNACE_BLOCK_ENTITY_TYPE,pos,state);
        this.modules = new ArrayList<>();
        HandiUtils.fill(modules,6, FurnaceModule::new);
        this.fuel = ItemStack.EMPTY;
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                switch (index) {
                    case 0:
                        return burnTime;
                    case 1:
                        return fuelTime;
                    default:
                        int x = index - 2;
                        if (x % 2 == 0) {
                            return modules.get(x / 2).cookTime;
                        } else {
                            return modules.get(x / 2).cookTimeTotal;
                        }
                }
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> burnTime = value;
                    case 1 -> fuelTime = value;
                    default -> {
                        int x = index - 2;
                        if (x % 2 == 0) {
                            modules.get(x / 2).cookTime = value;
                        } else {
                            modules.get(x / 2).cookTimeTotal = value;
                        }
                    }
                }
            }

            @Override
            public int size() {
                return modules.size() * 2 + 2;
            }
        };
    }


    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.netherite_furnace");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new NetheriteFurnaceScreenHandler(syncId,playerInventory,this,this.propertyDelegate);
    }

    public void tick() {
        boolean wasBurning = this.isBurning();
        boolean dirty = false;
        if (this.isBurning()) {
            --this.burnTime;
        }

        if (!this.world.isClient) {
            for (FurnaceModule m : modules) {
                if (!this.isBurning() && (fuel.isEmpty() || m.input.isEmpty())) {
                    m.decreaseCookTime();
                } else {
                    Recipe<?> recipe = this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, m.getInventory(), this.world).orElse(null);
                    if (!this.isBurning() && m.canAcceptRecipeOutput(recipe)) {
                        this.burnTime = this.getFuelTime(fuel);
                        this.fuelTime = this.burnTime;
                        if (this.isBurning()) {
                            dirty = true;
                            if (!fuel.isEmpty()) {
                                Item item = fuel.getItem();
                                fuel.decrement(1);
                                if (fuel.isEmpty()) {
                                    Item remainder = item.getRecipeRemainder();
                                    fuel = remainder == null ? ItemStack.EMPTY : new ItemStack(remainder);
                                }
                            }
                        }
                    }

                    if (this.isBurning() && m.canAcceptRecipeOutput(recipe)) {
                        ++m.cookTime;
                        if (m.cookTime == m.cookTimeTotal) {
                            m.cookTime = 0;
                            m.cookTimeTotal = m.getCookTime();
                            m.craftRecipe(recipe);
                            dirty = true;
                        }
                    } else {
                        m.cookTime = 0;
                    }
                }
            }

            if (wasBurning != this.isBurning()) {
                dirty = true;
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, this.isBurning()), 3);
            }
        }

        if (dirty) {
            this.markDirty();
        }

    }

    private int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) return 0;
        return AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(fuel.getItem(),0);
    }

    private boolean isBurning() {
        return burnTime > 0;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        burnTime = tag.getInt("BurnTime");
        if (tag.contains("Fuel",NbtType.COMPOUND)) {
            fuel = ItemStack.fromNbt(tag.getCompound("Fuel"));
        }
        fuelTime = getFuelTime(fuel);
        NbtList ms = tag.getList("Modules", NbtType.COMPOUND);
        int i = 0;
        for (FurnaceModule m : modules) {
            m.fromTag(ms.getCompound(i));
            i++;
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag.putShort("BurnTime", (short) burnTime);
        if (!fuel.isEmpty()) {
            tag.put("Fuel", fuel.writeNbt(new NbtCompound()));
        }
        NbtList list = new NbtList();
        for (FurnaceModule m : modules) {
            list.add(m.toTag(new NbtCompound()));
        }
        tag.put("Modules",list);
        return super.writeNbt(tag);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return OUTPUT_SLOTS;
        } else if (side == Direction.UP) {
            return INPUT_SLOTS;
        }
        return new int[]{0};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (slot == 0) return AbstractFurnaceBlockEntity.canUseAsFuel(stack) || (stack.getItem() == Items.BUCKET && fuel.getItem() != Items.BUCKET);
        if (slot < 7) {
            int min = getMinimumInputSlot();
            return min == slot;
        }
        return false;
    }

    private int getMinimumInputSlot() {
        int minCount = getMaxCountPerStack();
        int slot = -1;
        for (int i = 1; i < 7; i++) {
            ItemStack s = getStack(i);
            if (s.isEmpty()) {
                return i;
            }
            if (s.getCount() < minCount) {
                minCount = s.getCount();
                slot = i;
            }
        }
        return slot;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (dir == Direction.DOWN && slot == 0) {
            Item item = stack.getItem();
            return item == Items.WATER_BUCKET || item == Items.BUCKET;
        }
        return true;
    }

    @Override
    public int size() {
        return modules.size() * 2 + 1;
    }

    @Override
    public boolean isEmpty() {
        for (FurnaceModule m : modules) {
            if (!m.isEmpty()) return false;
        }
        return fuel.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot == 0) return fuel;
        if (slot < 7) return modules.get(slot - 1).input;
        return modules.get(slot - 7).output;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot >= 0 && slot < size()) {
            return getStack(slot).split(amount);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot >= 0 && slot < size()) {
            ItemStack stack = getStack(slot);
            setStackInModules(slot,ItemStack.EMPTY);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public void setStackInModules(int slot, ItemStack stack) {
        if (slot == 0) fuel = stack;
        else if (slot < 7) {
            modules.get(slot - 1).input = stack;
        } else {
            modules.get(slot - 7).output = stack;
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack prev = getStack(slot);
        boolean equal = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(prev) && ItemStack.areTagsEqual(stack,prev);
        setStackInModules(slot,stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        if (slot > 0 && slot < 7 && !equal) {
            FurnaceModule m = getModuleAt(slot);
            m.cookTimeTotal = m.getCookTime();
            m.cookTime = 0;
            markDirty();
        }
    }

    private FurnaceModule getModuleAt(int slot) {
        if (slot <= 0) return null;
        if (slot < 7) return modules.get(slot - 1);
        return modules.get(slot - 7);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (world.getBlockEntity(pos) != this) return false;
        return player.squaredDistanceTo(pos.getX() + 0.5,pos.getY() + 0.5,pos.getZ() + 0.5) <= 64;
    }

    @Override
    public void clear() {
        fuel = ItemStack.EMPTY;
        for (FurnaceModule m : modules) {
            m.clear();
        }
    }

    public void dropExperience(PlayerEntity player, int slot) {
        FurnaceModule m = getModuleAt(slot);
        if (m != null) {
            m.dropExperience(player);
        }
    }

    public void dropAllExperience(World world, Vec3d pos) {
        for (FurnaceModule m : modules) {
            m.dropXP(world,pos);
        }
    }

    public class FurnaceModule {

        private int cookTime;
        private int cookTimeTotal;
        private ItemStack input;
        private ItemStack output;
        private Object2IntOpenHashMap<Identifier> recipesUsed;

        public FurnaceModule() {
            input = ItemStack.EMPTY;
            output = ItemStack.EMPTY;
            recipesUsed = new Object2IntOpenHashMap<>();
        }

        public boolean isEmpty() {
            return input.isEmpty() && output.isEmpty();
        }

        public void clear() {
            input = ItemStack.EMPTY;
            output = ItemStack.EMPTY;
        }

        public void decreaseCookTime() {
            if (cookTime > 0) {
                cookTime = MathHelper.clamp(cookTime - 2,0,cookTimeTotal);
            }
        }

        public Inventory getInventory() {
            return new SimpleInventory(input);
        }

        public int getCookTime() {
            return world.getRecipeManager().getFirstMatch(RecipeType.SMELTING,getInventory(),world).map(AbstractCookingRecipe::getCookTime).orElse(200) / 3;
        }

        public boolean canAcceptRecipeOutput(Recipe<?> recipe) {
            if (!input.isEmpty() && recipe != null) {
                ItemStack itemStack = recipe instanceof CandySmeltingRecipe ? ((CandySmeltingRecipe) recipe).createRandomOutput(this) : recipe.getOutput();
                if (itemStack.isEmpty()) {
                    return false;
                } else {
                    if (output.isEmpty()) {
                        return true;
                    } else if (!output.isItemEqualIgnoreDamage(itemStack)) {
                        return false;
                    } else if (output.getCount() < getMaxCountPerStack() && output.getCount() < output.getMaxCount()) {
                        return true;
                    } else {
                        return output.getCount() < itemStack.getMaxCount();
                    }
                }
            } else {
                return false;
            }
        }

        public void craftRecipe(Recipe<?> recipe) {
            if (recipe != null && this.canAcceptRecipeOutput(recipe)) {
                ItemStack result = recipe instanceof CandySmeltingRecipe ? ((CandySmeltingRecipe) recipe).getRandomOutput(this) : recipe.getOutput();
                if (output.isEmpty()) {
                    output = result.copy();
                } else if (output.getItem() == result.getItem()) {
                    output.increment(1);
                }

                if (!world.isClient) {
                    addRecipeUsed(recipe);
                }

                if (input.getItem() == Blocks.WET_SPONGE.asItem() && !fuel.isEmpty() && fuel.getItem() == Items.BUCKET) {
                    fuel = new ItemStack(Items.WATER_BUCKET);
                }

                input.decrement(1);
            }
        }

        private void addRecipeUsed(Recipe<?> recipe) {
            recipesUsed.addTo(recipe.getId(),1);
        }

        public void fromTag(NbtCompound tag) {
            if (tag.contains("Input",NbtType.COMPOUND)) {
                input = ItemStack.fromNbt(tag.getCompound("Input"));
            }
            if (tag.contains("Output",NbtType.COMPOUND)) {
                output = ItemStack.fromNbt(tag.getCompound("Output"));
            }
            cookTime = tag.getShort("CookTime");
            cookTimeTotal = tag.getShort("CookTimeTotal");
            NbtCompound ru = tag.getCompound("RecipesUsed");

            for (String k : ru.getKeys()) {
                this.recipesUsed.put(new Identifier(k), ru.getInt(k));
            }
        }

        public NbtCompound toTag(NbtCompound tag) {
            if (!input.isEmpty()) {
                tag.put("Input",input.writeNbt(new NbtCompound()));
            }
            if (!output.isEmpty()) {
                tag.put("Output",output.writeNbt(new NbtCompound()));
            }
            tag.putShort("CookTime", (short) cookTime);
            tag.putShort("CookTimeTotal", (short) cookTimeTotal);
            NbtCompound ru = new NbtCompound();
            this.recipesUsed.forEach((identifier, integer) -> {
                ru.putInt(identifier.toString(), integer);
            });
            tag.put("RecipesUsed", ru);
            return tag;
        }

        public void dropExperience(PlayerEntity player) {
            List<Recipe<?>> recipes = dropXP(player.world,player.getPos());
            player.unlockRecipes(recipes);
            recipesUsed.clear();
        }

        private List<Recipe<?>> dropXP(World world, Vec3d pos) {
            List<Recipe<?>> recipes = new ArrayList<>();
            for (Object2IntMap.Entry<Identifier> e : recipesUsed.object2IntEntrySet()) {
                world.getRecipeManager().get(e.getKey()).ifPresent(recipe -> {
                    recipes.add(recipe);
                    dropXP(world,pos,e.getIntValue(), ((AbstractCookingRecipe) recipe).getExperience());
                });
            }
            return recipes;
        }

        private void dropXP(World world, Vec3d pos, int count, float experience) {
            int i = MathHelper.floor(count * experience);
            float g = MathHelper.fractionalPart(count * experience);
            if (g != 0 && Math.random() < g) {
                i++;
            }
            while (i > 0) {
                int size = ExperienceOrbEntity.roundToOrbSize(i);
                i -= size;
                world.spawnEntity(new ExperienceOrbEntity(world,pos.x,pos.y,pos.z,size));
            }
        }
    }
}
