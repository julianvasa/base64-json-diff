package com.juli.enums;

public enum Side {
	LEFT, RIGHT;

	@Override
	public String toString() {
		switch (this) {
		case LEFT:
			return "LEFT";
		case RIGHT:
			return "RIGHT";
		}
		throw new Error("An error occurred while trying to get the correct side.");
	}
}
