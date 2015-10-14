package com.carltian.frame.container.reg;

public class RegistrationKey {
	private final RegistrationType type;
	private final String extType;
	private final String name;
	private final int hashCode;

	public RegistrationKey(RegistrationType type, String extType, String name) {
		this.type = type;
		this.extType = extType;
		this.name = name;
		int typeHash = 0;
		if (type != null) {
			typeHash = type.hashCode();
		}
		int extTypeHash = 0;
		if (extType != null) {
			extTypeHash = extType.hashCode();
		}
		int nameHash = 0;
		if (name != null) {
			nameHash = name.hashCode();
		}
		hashCode = 31 * (typeHash * 31 + extTypeHash) + nameHash;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof RegistrationKey) {
			RegistrationKey target = (RegistrationKey) obj;
			return ((type == null && target.type == null) || (type != null && type.equals(target.type)))
					&& ((extType == null && target.extType == null) || (extType != null && extType.equals(target.extType)))
					&& ((name == null && target.name == null) || (name != null && name.equals(target.name)));
		}
		return false;
	}
}
