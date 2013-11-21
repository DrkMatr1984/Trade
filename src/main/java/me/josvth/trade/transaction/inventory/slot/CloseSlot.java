package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CloseSlot extends Slot {

	private final ItemDescription closeDescription;

	public CloseSlot(int slot, ItemDescription closeDescription) {
		super(slot);
		this.closeDescription = closeDescription;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

		holder.getTrader().closeInventory();

		event.setCancelled(true);

	}

	@Override
	public void update(TransactionHolder holder) {
		setSlot(holder, closeDescription.create());
	}

}
