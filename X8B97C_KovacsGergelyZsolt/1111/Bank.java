public class Bank {
	private int loans;

	public synchronized void takeLoan(int amount) {
		loans += amount;
	}

	public synchronized int getLoans() {
		return loans;
	}
}
