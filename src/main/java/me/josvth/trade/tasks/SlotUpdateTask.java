package me.josvth.trade.tasks;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.Slot;

import java.util.Set;

public class SlotUpdateTask implements Runnable {

	private final TransactionHolder holder;
	private final Slot[] slot;

	public SlotUpdateTask(TransactionHolder holder, Set<? extends Slot> slots) {
		this(holder, slots.toArray(new Slot[slots.size()]));
	}

	public SlotUpdateTask(TransactionHolder holder, Slot... slot) {
		this.holder = holder;
		this.slot = slot;
	}

	@Override
	public void run() {
		for (Slot s : slot) {
			s.update(holder);
		}
	}

}
