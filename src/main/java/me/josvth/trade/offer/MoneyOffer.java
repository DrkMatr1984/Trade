package me.josvth.trade.offer;

import me.josvth.trade.transaction.Trader;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MoneyOffer extends Offer {

	private double amount = 0;

	public MoneyOffer(OfferList list, int offerID) {
		this(list, offerID, 0);
	}

	public MoneyOffer(OfferList list, int offerID, double amount) {
		super(list, offerID);
		this.amount = amount;
	}

	@Override
	public <T extends Offer> T add(T tradeable) {

		if (!(tradeable instanceof MoneyOffer))
			return tradeable;

		MoneyOffer remaining = ((MoneyOffer) tradeable).clone();

		amount += remaining.getAmount();

		return null;

	}

	@Override
	public <T extends Offer> T remove(T tradeable) {

		if (!(tradeable instanceof MoneyOffer)) {
			return tradeable;
		}

		MoneyOffer remaining = ((MoneyOffer) tradeable).clone();

		amount -= remaining.getAmount();

		if (amount < 0) {
			amount = 0.0;
			remaining.setAmount(-1 * amount);
			return (T) remaining;
		} else {
			return null;
		}

	}

	@Override
	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	protected MoneyOffer clone() {
		return new MoneyOffer(list, offerID, amount);
	}

	@Override
	public ItemStack getDisplayItem() {

		final ItemStack item = new ItemStack(Material.GOLD_INGOT, 0);

		final ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(amount + " monies");

		item.setItemMeta(meta);

		return item;

	}

	@Override
	public void grant(Trader trader) {

	}

}
