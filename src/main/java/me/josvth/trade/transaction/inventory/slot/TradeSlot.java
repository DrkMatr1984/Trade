package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.offer.ItemOffer;
import me.josvth.trade.offer.Offer;
import me.josvth.trade.tasks.SlotUpdateTask;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.Set;

public class TradeSlot extends Slot {

	private final int offerIndex;

	public TradeSlot(int slot, int offerIndex) {
		super(slot);
		this.offerIndex = offerIndex;
	}

	public Offer getContents(TransactionHolder holder) {
		return holder.getOffers().get(offerIndex);
	}

	public int getOfferIndex() {
		return offerIndex;
	}

	// Event handling
	@Override
	public void onClick(InventoryClickEvent event) {

        final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

		final Offer offer = getContents(holder);

		// We cancel the others accept if they had accepted
		if (holder.getOtherTrader().hasAccepted()) {
			holder.getOtherTrader().setAccepted(false);

			// Update slots
			AcceptSlot.updateAcceptSlots(holder.getOtherHolder(), true);
			StatusSlot.updateStatusSlots(holder, true);

			// Notify player
			Trade.getInstance().getFormatManager().getMessage("trading.offer-changed").send(holder.getOtherTrader().getPlayer(), "%player%", holder.getTrader().getName());
		}

		// If we have a offer on this slot we let the offer handle the event
		if (offer == null) {

			// TODO An alternative would be to determine the new item in the update() method instead of during the event.

			ItemStack newItem = null;

			switch (event.getAction()) {
				case PLACE_ALL:
					newItem = event.getCursor().clone();
					break;
				case PLACE_ONE:
					newItem = event.getCursor().clone();
					newItem.setAmount(1);
					break;
				default:
					throw new IllegalStateException("Not handled action: " + event.getAction().name());
			}

			holder.getOffers().set(offerIndex, (newItem == null)? null : createItemOffer(holder, newItem));

			MirrorSlot.updateMirrors(holder.getOtherHolder(), true);

		} else {

			offer.onClick(event);

		}

	}

	@Override
	public void onDrag(InventoryDragEvent event) {

		final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

		final Offer offer = getContents(holder);

		if (offer != null) {
			offer.onDrag(slot, event);
		} else if (event.getNewItems().containsKey(slot)) {

			holder.getOffers().set(offerIndex, createItemOffer(holder, event.getNewItems().get(slot).clone()));

			MirrorSlot.updateMirrors(holder.getOtherHolder(), true, offerIndex);

		}

	}

	@Override
	public void update(TransactionHolder holder) {

		final Offer offer = getContents(holder);

		if (offer != null) {
			holder.getInventory().setItem(slot, offer.getDisplayItem());
		} else {
			holder.getInventory().setItem(slot, null);
		}

	}



    private ItemOffer createItemOffer(TransactionHolder holder, ItemStack itemStack) {
        final ItemOffer offer = holder.getLayout().createOffer(ItemOffer.class, holder.getOffers(), offerIndex);
        offer.setItem(itemStack);
        return offer;
    }

	public static void updateTradeSlots(TransactionHolder holder, boolean nextTick, int... offerIndex) {

		final Set<TradeSlot> slots = holder.getLayout().getSlotsOfType(TradeSlot.class);

		final Iterator<TradeSlot> iterator = slots.iterator();

		while (iterator.hasNext()) {

            final TradeSlot slot = iterator.next();

            boolean notUpdated = true;
            for (int i = 0; i < offerIndex.length && notUpdated; i++) {
                if (slot.getOfferIndex() == offerIndex[i]) {
                    if (!nextTick) {
                        slot.update(holder);
                    }
                    notUpdated = false;
                }
            }

            if (notUpdated) {
                iterator.remove();
            }

		}

		if (nextTick && !slots.isEmpty()) {
			Bukkit.getScheduler().runTask(Trade.getInstance(), new SlotUpdateTask(holder, slots));
		}

	}

	public static void updateTradeSlots(TransactionHolder holder, boolean nextTick) {

		final Set<TradeSlot> slots = holder.getLayout().getSlotsOfType(TradeSlot.class);

		if (!nextTick) {
			for (Slot slot : slots) {
				slot.update(holder);
			}
		} else if (!slots.isEmpty()) {
			Bukkit.getScheduler().runTask(Trade.getInstance(), new SlotUpdateTask(holder, slots));
		}

	}


}
