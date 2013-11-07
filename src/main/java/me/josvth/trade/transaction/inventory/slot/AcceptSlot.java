package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.tasks.SlotUpdateTask;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.Set;

public class AcceptSlot extends Slot {

	private final ItemStack acceptItem;
	private final ItemStack pendingItem;

	private final boolean keepMeta;

	public AcceptSlot(int slot, ItemStack acceptItem, ItemStack pendingItem, boolean keepMeta) {
		super(slot);
		this.acceptItem = acceptItem;
		this.pendingItem = pendingItem;
		this.keepMeta = keepMeta;
	}
	@Override
	public void onClick(InventoryClickEvent event) {

		final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

		holder.getTrader().setAccepted(!holder.getTrader().hasAccepted());

		AcceptSlot.updateAcceptSlots(holder, true);
		StatusSlot.updateStatusSlots(holder, true);

		event.setCancelled(true);

	}

	public static void updateAcceptSlots(TransactionHolder holder, boolean nextTick) {

		final Set<AcceptSlot> slots = holder.getLayout().getSlotsOfType(AcceptSlot.class);

		if (!nextTick) {
		   for (Slot slot : slots) {
			   slot.update(holder);
		   }
		} else if (!slots.isEmpty()) {
			Bukkit.getScheduler().runTask(Trade.getInstance(), new SlotUpdateTask(holder, slots));
		}

	}

	public void update(TransactionHolder holder) {

		if (holder.getTrader().hasAccepted()) {
			setSlot(holder, pendingItem);
		} else {
			setSlot(holder, acceptItem);
		}

	}

}
