package io.openems.edge.battery.protection.force;

import java.time.Duration;

public class ForceCharge extends AbstractForceChargeDischarge {

	/**
	 * Holds parameters for 'Force Charge' mode.
	 */
	public static class Params {
		private final int startChargeBelowCellVoltage;
		private final int chargeBelowCellVoltage;
		private final int blockDischargeBelowCellVoltage;

		public Params(int startChargeBelowCellVoltage, int chargeBelowCellVoltage, int blockDischargeBelowCellVoltage) {
			if (blockDischargeBelowCellVoltage < chargeBelowCellVoltage
					|| chargeBelowCellVoltage < startChargeBelowCellVoltage) {
				throw new IllegalArgumentException(
						"Make sure that startChargeBelowCellVoltage < chargeBelowCellVoltage < blockDischargeBelowCellVoltage.");
			}

			this.startChargeBelowCellVoltage = startChargeBelowCellVoltage;
			this.chargeBelowCellVoltage = chargeBelowCellVoltage;
			this.blockDischargeBelowCellVoltage = blockDischargeBelowCellVoltage;
		}
	}

	private final Params params;

	public ForceCharge(Params params) {
		super();
		this.params = params;
	}

	@Override
	protected State handleUndefinedState(int minCellVoltage, int maxCellVoltage) {
		if (minCellVoltage <= this.params.startChargeBelowCellVoltage) {
			return State.WAIT_FOR_FORCE_MODE;
		} else {
			return State.UNDEFINED;
		}
	}

	@Override
	protected State handleWaitForForceModeState(int minCellVoltage, int maxCellVoltage, Duration durationSinceStart) {
		if (maxCellVoltage > this.params.startChargeBelowCellVoltage) {
			return State.UNDEFINED;

		} else if (durationSinceStart.getSeconds() < WAIT_FOR_FORCE_MODE_SECONDS) {
			return State.WAIT_FOR_FORCE_MODE;

		} else {
			return State.FORCE_MODE;
		}
	}

	@Override
	protected State handleForceModeState(int minCellVoltage, int maxCellVoltage) {
		if (minCellVoltage <= this.params.chargeBelowCellVoltage) {
			return State.FORCE_MODE;
		} else {
			return State.BLOCK_MODE;
		}
	}

	@Override
	protected State handleBlockModeState(int minCellVoltage, int maxCellVoltage) {
		if (maxCellVoltage <= this.params.blockDischargeBelowCellVoltage) {
			return State.BLOCK_MODE;
		} else {
			return State.UNDEFINED;
		}
	}
}