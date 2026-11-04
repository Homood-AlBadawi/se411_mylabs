package edu.psu.se411.lab11_threads.model;

import java.util.concurrent.atomic.AtomicInteger;

import edu.psu.se411.lab11_threads.exceptions.InsufficientBalanceException;

/**
 * A payment from one account to another.
 *
 * <p>Two ways to run it:
 * <ul>
 *   <li>{@link #execute()} - part 1. No synchronization at all, so concurrent
 *       transactions corrupt the balances.</li>
 *   <li>{@link #executeSynchronized(Transaction)} - part 2. One
 *       {@code synchronized} method, and the total is conserved.</li>
 * </ul>
 */
public class Transaction {

	/** Supplies ids when the caller does not give one. */
	private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

	private String id;
	private CryptoAccount sender;
	private CryptoAccount receiver;
	private Double amount;

	public Transaction(String id, CryptoAccount sender, CryptoAccount receiver, Double amount) {
		super();
		if (id == null || sender == null || receiver == null || amount < 0) {
			throw new IllegalArgumentException();
		}
		this.id = id;
		this.sender = sender;
		this.receiver = receiver;
		this.amount = amount;
	}

	/**
	 * The constructor the lab's sample code calls:
	 * {@code new Transaction(randomA, randomB, randomAmount)}.
	 *
	 * An id is generated, so callers that do not care about one do not have to
	 * invent it.
	 */
	public Transaction(CryptoAccount sender, CryptoAccount receiver, Double amount) {
		this("tx_" + NEXT_ID.getAndIncrement(), sender, receiver, amount);
	}

	// ==================================================================
	// Part 1 - no synchronization
	// ==================================================================

	/**
	 * Moves the money with no locking whatsoever.
	 *
	 * Two races are possible here, and both corrupt the total:
	 * <ol>
	 *   <li>inside a single account, two threads read the same balance and one
	 *       write overwrites the other;</li>
	 *   <li>between the debit and the credit, this transaction is not atomic -
	 *       another thread can observe the system mid-transfer.</li>
	 * </ol>
	 */
	public void execute() {
		transfer();
	}

	// ==================================================================
	// Part 2 - one synchronized method
	// ==================================================================

	/**
	 * The fix: a single {@code synchronized} method.
	 *
	 * <p>It is <b>static</b> on purpose. A {@code synchronized} <i>instance</i>
	 * method would lock {@code this} - but every transaction is a different
	 * object, so ten thousand transactions would take ten thousand different
	 * locks and exclude nobody. A static synchronized method locks the one
	 * {@code Transaction.class} object instead, so every transfer in the system
	 * serialises against every other.
	 *
	 * <p>Making {@code CryptoAccount}'s own methods synchronized would not fix
	 * it either: that makes each debit and each credit atomic, but the pair of
	 * them still is not, so another thread can still see the money after it has
	 * left the sender and before it reaches the receiver.
	 */
	public static synchronized void executeSynchronized(Transaction transaction) {
		transaction.transfer();
	}

	/** The actual work, shared by both paths. */
	private void transfer() {
		try {
			sender.retrieveFromBalance(amount);
			receiver.addToBalance(amount);
		} catch (InsufficientBalanceException e) {
			// The sender could not pay, so nothing was credited: the system
			// total is unaffected and the transaction is simply skipped.
		}
	}

	public CryptoAccount getSender() {
		return sender;
	}

	public void setSender(CryptoAccount sender) {
		this.sender = sender;
	}

	public CryptoAccount getReceiver() {
		return receiver;
	}

	public void setReceiver(CryptoAccount receiver) {
		this.receiver = receiver;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getId() {
		return id;
	}
}
