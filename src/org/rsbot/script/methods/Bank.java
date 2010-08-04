package org.rsbot.script.methods;

import org.rsbot.script.wrappers.*;

import java.awt.*;

/**
 * Bank related operations.
 */
public class Bank extends MethodProvider {

	public static final int[] BANKERS = { 44, 45, 494, 495, 499, 958, 1036,
			2271, 2354, 2355, 3824, 5488, 5901, 5912, 5913, 6362, 6532, 6533,
			6534, 6535, 7605, 8948, 14367, };
	public static final int[] BANK_BOOTHS = { 2213, 4483, 6084, 11402, 11758,
			12759, 14367, 19230, 24914, 25808, 26972, 27663, 29085, 34752,
			35647, };
	public static final int[] BANK_CHESTS = { 4483, 12308, 21301, 27663, 42192, };
	public static final int[] BANK_DEPOSIT_BOX = { 9398, 20228, 26969, 36788, };

	public static final int[] DO_NOT_DEPOSIT = new int[] {
		1265, 1267, 1269, 1273, 1271, 1275, 1351, 590, 303 };

	public static final int INTERFACE_BANK = 762;
	public static final int INTERFACE_BANK_BUTTON_CLOSE = 43;
	public static final int INTERFACE_BANK_BUTTON_DEPOSIT_BEAST_INVENTORY = 38;
	public static final int INTERFACE_BANK_BUTTON_DEPOSIT_CARRIED_ITEMS = 34;
	public static final int INTERFACE_BANK_BUTTON_DEPOSIT_WORN_ITEMS = 36;
	public static final int INTERFACE_BANK_BUTTON_HELP = 44;
	public static final int INTERFACE_BANK_BUTTON_INSERT = 15;
	public static final int INTERFACE_BANK_BUTTON_ITEM = 19;
	public static final int INTERFACE_BANK_BUTTON_NOTE = 19;
	public static final int INTERFACE_BANK_BUTTON_SEARCH = 17;
	public static final int INTERFACE_BANK_BUTTON_SWAP = 15;
	public static final int INTERFACE_BANK_INVENTORY = 93;
	public static final int INTERFACE_BANK_ITEM_FREE_COUNT = 29;
	public static final int INTERFACE_BANK_ITEM_FREE_MAX = 30;
	public static final int INTERFACE_BANK_ITEM_MEMBERS_COUNT = 31;
	public static final int INTERFACE_BANK_ITEM_MEMBERS_MAX = 32;
	public static final int INTERFACE_BANK_SCROLLBAR = 114;
	public static final int INTERFACE_BANK_SEARCH = 752;
	public static final int INTERFACE_BANK_SEARCH_INPUT = 5;

	public static final int INTERFACE_COLLECTION_BOX = 105;
	public static final int INTERFACE_COLLECTION_BOX_CLOSE = 13;

	public static final int[] INTERFACE_BANK_TAB = { 61, 59, 57, 55, 53, 51,
			49, 47, 45 };
	public static final int[] INTERFACE_BANK_TAB_FIRST_ITEM = { 78, 79, 80, 81,
			82, 83, 84, 85, 86 };

	public static final int INTERFACE_DEPOSIT_BOX = 11;
	public static final int INTERFACE_DEPOSIT_BOX_INTERFACE = 61;
	public static final int INTERFACE_DEPOSIT_BOX_BUTTON_CLOSE = 62;

	Bank(final MethodContext ctx) {
		super(ctx);
	}

	/**
	 * Performs a given action on the specified item ID within the bank.
	 * 
	 * @param itemID The ID of the item.
	 * @param txt The action to perform.
	 * @return <tt>true</tt> if successful; otherwise <tt>false</tt>.
	 */
	public boolean atItem(final int itemID, final String txt) {
		if (!methods.game.isLoggedIn() || !isOpen())
			return false;
		final RSItem item = getItem(itemID);
		return (item != null) && item.isComponentValid() && item.doAction(txt);
	}

	/**
	 * Closes the bank interface.
	 * 
	 * @return <tt>true</tt> if the bank interface is no longer open.
	 */
	public boolean close() {
		if (!isOpen())
			return true;
		methods.interfaces
				.getComponent(INTERFACE_BANK, INTERFACE_BANK_BUTTON_CLOSE)
				.doClick();
		sleep(random(500, 600));
		return !isOpen();
	}

	/**
	 * If bank is open, deposits specified amount of an item into the bank.
	 * 
	 * @param itemID
	 *            The ID of the item.
	 * @param numberToDeposit
	 *            The amount to deposit. 0 deposits All. 1,5,10 deposit
	 *            corresponding amount while other numbers deposit X.
	 * @return <tt>true</tt> if successful; otherwise <tt>false</tt>.
	 */
	public boolean deposit(final int itemID, final int numberToDeposit) {
		RSItem item = methods.inventory.getItem(itemID);
		if (numberToDeposit < 0)
			throw new IllegalArgumentException("numberToDepsoit < 0 ("
					+ numberToDeposit + ")");
		if (!isOpen())
			return false;
		final int inventoryCount = methods.inventory.getCount(true);
		switch (numberToDeposit) {
		case 0: // Deposit All
			item.doAction("Deposit-All");
			break;
		case 1: // Deposit 1
			item.doAction("Deposit-1");
			break;
		case 5: // Deposit 5
			item.doAction("Deposit-5");
			break;
		case 10: // Deposit 10
			item.doAction("Deposit-10");
			break;
		default: // Deposit x
			if (item.doAction("Deposit-" + numberToDeposit)) {
				if (item.doAction("Deposit-X")) {
					sleep(random(1000, 1300));
					methods.inputManager.sendKeys("" + numberToDeposit, true);
				}
			}
			break;
		}
		return (methods.inventory.getCount(true) < inventoryCount)
				|| (methods.inventory.getCount() == 0);
	}

	/**
	 * Deposits all items in methods.inventory.
	 * 
	 * @return <tt>true</tt> on success.
	 */
	public boolean depositAll() {
		return isOpen()
				&& methods.interfaces.getComponent(INTERFACE_BANK,
						INTERFACE_BANK_BUTTON_DEPOSIT_CARRIED_ITEMS).doClick();
	}

	/**
	 * Deposit everything your player has equipped.
	 * 
	 * @return <tt>true</tt> on success.
	 * @since 6 March 2009.
	 */
	public boolean depositAllEquipped() {
		return isOpen()
				&& methods.interfaces.getComponent(INTERFACE_BANK,
						INTERFACE_BANK_BUTTON_DEPOSIT_WORN_ITEMS).doClick();
	}

	/**
	 * Deposits all items in inventory except for the given IDs.
	 * 
	 * @param items The items not to deposit.
	 * @return <tt>true</tt> on success.
	 */
	public boolean depositAllExcept(final int... items) {
		int inventoryCount = methods.inventory.getCount();
		RSItem[] inventoryArray = methods.inventory.getItems();
		outer: for (int off = 0; off < inventoryArray.length; off++) {
			if (inventoryArray[off].getID() == -1) {
				continue;
			}
			for (final int item : items) {
				if (inventoryArray[off].getID() == item) {
					continue outer;
				}
			}

			for (int tries = 0; tries < 5; tries++) {
				inventoryArray[off].doAction("Deposit-All");
				sleep(random(500, 700));
				if (methods.inventory.getCount() < inventoryCount) {
					break;
				}
			}
			if (methods.inventory.getCount() >= inventoryCount)
				return false;
			inventoryArray = methods.inventory.getItems();
			inventoryCount = methods.inventory.getCount();
		}
		return true;
	}

	/**
	 * Deposits everything your familiar is carrying.
	 * 
	 * @return <tt>true</tt> on success
	 * @since 6 March 2009.
	 */
	public boolean depositAllFamiliar() {
		return isOpen()
				&& methods.interfaces.getComponent(INTERFACE_BANK,
						INTERFACE_BANK_BUTTON_DEPOSIT_BEAST_INVENTORY)
						.doClick();
	}

	/**
	 * Returns the sum of the count of the given items in the bank.
	 * 
	 * @param items The array of items.
	 * @return The sum of the stacks of the items.
	 */
	public int getCount(final int... items) {
		int itemCount = 0;
		final RSItem[] inventoryArray = getItems();
		for (RSItem item : inventoryArray) {
			for (final int id : items) {
				if (item.getID() == id) {
					itemCount += item.getStackSize();
				}
			}
		}
		return itemCount;
	}

	/**
	 * Get current tab open in the bank.
	 * 
	 * @return int of tab (0-8), or -1 if none are selected (bank is not open).
	 */
	public int getCurrentTab() {
		for (int i = 0; i < INTERFACE_BANK_TAB.length; i++) {
			if (methods.interfaces.get(INTERFACE_BANK).getComponent(
					INTERFACE_BANK_TAB[i] - 1).getBackgroundColor() == 1419)
				return i;
		}
		return -1; // no selected ones. Bank may not be open.
	}

	/**
	 * Gets the bank interface.
	 * 
	 * @return The bank <code>RSInterface</code>.
	 */
	public RSInterface getInterface() {
		return methods.interfaces.get(INTERFACE_BANK);
	}

	/**
	 * Gets the <code>RSComponent</code> of the given item at the
	 * specified index.
	 * 
	 * @param index The index of the item.
	 * @return <code>RSComponent</code> if item is found at index;
	 * otherwise null.
	 */
	public RSItem getItemAt(final int index) {
		final RSItem[] items = getItems();
		if (items != null) {
			for (final RSItem item : items) {
				if (item.getComponent().getComponentIndex() == index)
					return item;
			}
		}

		return null;
	}

	/**
	 * Gets the first item with the provided ID in the bank.
	 * 
	 * @param id ID of the item to get.
	 * @return The component of the item; otherwise null.
	 */
	public RSItem getItem(final int id) {
		final RSItem[] items = getItems();
		if (items != null) {
			for (final RSItem item : items) {
				if (item.getID() == id)
					return item;
			}
		}
		return null;
	}

	/**
	 * Gets the point on the screen for a given item.
	 * Numbered left to right then top to bottom.
	 * 
	 * @param slot The index of the item.
	 * @return The point of the item or new Point(-1, -1) if null.
	 */
	public Point getItemPoint(final int slot) {
		if (slot < 0)
			throw new IllegalArgumentException("slot < 0 " + slot);

		final RSItem item = getItemAt(slot);
		if (item != null)
			return item.getComponent().getLocation();

		return new Point(-1, -1);
	}

	/**
	 * Gets all the items in the bank's inventory.
	 * 
	 * @return an <code>RSItem</code> array of the bank's inventory interface.
	 */
	public RSItem[] getItems() {
		if ((getInterface() == null)
				|| (getInterface().getComponent(INTERFACE_BANK_INVENTORY) == null))
			return new RSItem[0];

		RSComponent[] components = getInterface().getComponent(
				INTERFACE_BANK_INVENTORY).getComponents();
		RSItem[] items = new RSItem[components.length];
		for (int i = 0; i < items.length; ++i) {
			items[i] = new RSItem(methods, components[i]);
		}
		return items;
	}

	/**
	 * Checks whether or not the bank is open.
	 * 
	 * @return <tt>true</tt> if the bank interface is open; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean isOpen() {
		return getInterface().isValid();
	}

	/**
	 * Opens one of the supported banker NPCs, booths, or chests nearby. If they
	 * are not nearby, and they are not null, it will automatically walk to the
	 * closest one.
	 * 
	 * @return <tt>true</tt> if the bank was opened; otherwise <tt>false</tt>.
	 */
	public boolean open() {
		try {
			if (!isOpen()) {
				if (methods.menu.isOpen()) {
					methods.mouse.moveSlightly();
					sleep(random(20, 30));
				}
				RSObject bankBooth = methods.objects.getNearest(BANK_BOOTHS);
				RSNPC banker = methods.npcs.getNearest(BANKERS);
				final RSObject bankChest = methods.objects
						.getNearest(BANK_CHESTS);
				int lowestDist = methods.calc.distanceTo(bankBooth);
				if ((banker != null)
						&& (methods.calc.distanceTo(banker) < lowestDist)) {
					lowestDist = methods.calc.distanceTo(banker);
					bankBooth = null;
				}
				if ((bankChest != null)
						&& (methods.calc.distanceTo(bankChest) < lowestDist)) {
					bankBooth = null;
					banker = null;
				}
				if (((bankBooth != null)
						&& (methods.calc.distanceTo(bankBooth) < 8)
						&& methods.calc.tileOnMap(bankBooth.getLocation()) && methods.calc
						.canReach(bankBooth.getLocation(), true))
						|| ((banker != null)
								&& (methods.calc.distanceTo(banker) < 8)
								&& methods.calc.tileOnMap(banker.getLocation()) && methods.calc
								.canReach(banker.getLocation(), true))
						|| ((bankChest != null)
								&& (methods.calc.distanceTo(bankChest) < 8)
								&& methods.calc.tileOnMap(bankChest
										.getLocation())
								&& methods.calc.canReach(bankChest
										.getLocation(), true) && !isOpen())) {
					if (bankBooth != null) {
						if (bankBooth.doAction("Use-Quickly")) {
							int count = 0;
							while (!isOpen() && ++count < 10) {
								sleep(random(200, 400));
								if (methods.players.getMyPlayer().isMoving()) {
									count = 0;
								}
							}
						} else {
							methods.camera.turnToObject(bankBooth);
						}
					} else if (banker != null) {
						if (banker.doAction("Bank ")) {
							int count = 0;
							while (!isOpen() && ++count < 10) {
								sleep(random(200, 400));
								if (methods.players.getMyPlayer().isMoving()) {
									count = 0;
								}
							}
						} else {
							methods.camera.turnToCharacter(banker, 20);
						}
					} else if (bankChest != null) {
						if (bankChest.doAction("Bank")) {
							int count = 0;
							while (!isOpen() && ++count < 10) {
								sleep(random(200, 400));
								if (methods.players.getMyPlayer().isMoving()) {
									count = 0;
								}
							}
						} else {
							methods.camera.turnToObject(bankChest);
						}
					}
				} else {
					if (bankBooth != null) {
						methods.walking.walkTo(bankBooth.getLocation());
					} else if (banker != null) {
						methods.walking.walkTo(banker.getLocation());
					} else if (bankChest != null) {
						methods.walking.walkTo(bankChest.getLocation());
					}
				}
			}
			return isOpen();
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @return <tt>true</tt> if currently searching the bank.
	 */
	public boolean isSearchOpen() {
		// Setting 1248 is -2147483648 when search is enabled and -2013265920
		return (methods.settings.getSetting(1248) == -2147483648);
	}

	/**
	 * Searches for an item in the bank. Returns true if succeeded (does not
	 * necessarily mean it was found).
	 * 
	 * @param itemName
	 *            The item name to find.
	 * @return <tt>true</tt> on success.
	 */
	public boolean searchItem(final String itemName) {
		if (!isOpen())
			return false;

		methods.interfaces.getComponent(INTERFACE_BANK,
				INTERFACE_BANK_BUTTON_SEARCH).doAction("Search");
		sleep(random(1000, 1500));
		if (!isSearchOpen()) {
			sleep(500);
		}
		if (isOpen() && isSearchOpen()) {
			methods.inputManager.sendKeys(itemName, false);
			sleep(random(300, 700));
			return true;
		}
		return false;
	}

	/**
	 * Sets the bank rearrange mode to insert.
	 * 
	 * @return <tt>true</tt> on success.
	 */
	public boolean setRearrangeModeToInsert() {
		if (!isOpen())
			return false;
		if (methods.settings
				.getSetting(Settings.SETTING_BANK_TOGGLE_REARRANGE_MODE) != 1) {
			methods.interfaces.getComponent(INTERFACE_BANK,
					INTERFACE_BANK_BUTTON_INSERT).doClick();
			sleep(random(500, 700));
		}
		return methods.settings
				.getSetting(Settings.SETTING_BANK_TOGGLE_REARRANGE_MODE) == 1;
	}

	/**
	 * Sets the bank rearrange mode to swap.
	 * 
	 * @return <tt>true</tt> on success.
	 */
	public boolean setRearrangeModeToSwap() {
		if (!isOpen())
			return false;
		if (methods.settings
				.getSetting(Settings.SETTING_BANK_TOGGLE_REARRANGE_MODE) != 0) {
			methods.interfaces.getComponent(INTERFACE_BANK,
					INTERFACE_BANK_BUTTON_SWAP).doClick();
			sleep(random(500, 700));
		}
		return methods.settings
				.getSetting(Settings.SETTING_BANK_TOGGLE_REARRANGE_MODE) == 0;
	}

	/**
	 * Sets the bank withdraw mode to item.
	 * 
	 * @return <tt>true</tt> on success.
	 */
	public boolean setWithdrawModeToItem() {
		if (!isOpen())
			return false;
		if (methods.settings
				.getSetting(Settings.SETTING_BANK_TOGGLE_WITHDRAW_MODE) != 0) {
			methods.interfaces.getComponent(INTERFACE_BANK,
					INTERFACE_BANK_BUTTON_ITEM).doClick();
			sleep(random(500, 700));
		}
		return methods.settings
				.getSetting(Settings.SETTING_BANK_TOGGLE_WITHDRAW_MODE) == 0;
	}

	/**
	 * Sets the bank withdraw mode to note.
	 * 
	 * @return <tt>true</tt> on success.
	 */
	public boolean setWithdrawModeToNote() {
		if (!isOpen())
			return false;
		if (methods.settings
				.getSetting(Settings.SETTING_BANK_TOGGLE_WITHDRAW_MODE) != 1) {
			methods.interfaces.getComponent(INTERFACE_BANK,
					INTERFACE_BANK_BUTTON_NOTE).doClick();
			sleep(random(500, 700));
		}
		return methods.settings
				.getSetting(Settings.SETTING_BANK_TOGGLE_WITHDRAW_MODE) == 1;
	}

	/**
	 * Tries to withdraw an item.
	 * <p/>
	 * 0 is All. 1,5,10 use Withdraw 1,5,10 while other numbers Withdraw X.
	 * 
	 * @param itemID
	 *            The ID of the item.
	 * @param count
	 *            The number to withdraw.
	 * @return <tt>true</tt> on success.
	 */
	public boolean withdraw(final int itemID, final int count) {
		if (count < 0)
			throw new IllegalArgumentException("count < 0 (" + count + ")");
		if (!isOpen())
			return false;
		final RSItem item = getItem(itemID);
		if (item == null || !item.isComponentValid())
			return false;
		final int inventoryCount = methods.inventory.getCount(true);
		switch (count) {
		case 0: // Withdraw All
			item.doAction("Withdraw-All");
			break;
		case 1: // Withdraw 1
            item.doClick(true);
            break;
		case 5: // Withdraw 5
		case 10: // Withdraw 10
			item.doAction("Withdraw-" + count);
			break;
		default: // Withdraw x
			if (item.doClick(false)) {
				sleep(random(600, 900));
				String[] mactions = methods.menu.getActions();
				boolean found = false;
				for (int i = 0; i < mactions.length; i++) {
					if (mactions[i].equalsIgnoreCase("Withdraw-" + count)) {
						found = true;
						methods.menu.clickIndex(i);
						break;
					}
				}
				if (!found && item.doAction("Withdraw-X")) {
					sleep(random(1000, 1300));
					methods.inputManager.sendKeys("" + count, true);
				}
			}
			break;
		}
		return (methods.inventory.getCount(true) > inventoryCount)
				|| (methods.inventory.getCount(true) == 28);
	}
}
