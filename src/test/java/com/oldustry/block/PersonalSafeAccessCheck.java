package com.oldustry.block;

import java.util.UUID;

public final class PersonalSafeAccessCheck {
    public static void main(String[] args) {
        UUID owner = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID other = UUID.fromString("22222222-2222-2222-2222-222222222222");

        assert PersonalSafeAccess.open(null, owner, false) == PersonalSafeAccess.Verdict.CLAIM;
        assert PersonalSafeAccess.open(owner, owner, false) == PersonalSafeAccess.Verdict.ALLOW;
        assert PersonalSafeAccess.open(owner, other, false) == PersonalSafeAccess.Verdict.DENY;
        assert PersonalSafeAccess.open(owner, other, true) == PersonalSafeAccess.Verdict.ALLOW;
        assert PersonalSafeAccess.open(owner, null, true) == PersonalSafeAccess.Verdict.DENY;

        assert !PersonalSafeAccess.wrenchRemoves(PersonalSafeAccess.Verdict.DENY, true);
        assert !PersonalSafeAccess.wrenchRemoves(PersonalSafeAccess.Verdict.ALLOW, false);
        assert PersonalSafeAccess.wrenchRemoves(PersonalSafeAccess.Verdict.CLAIM, true);
        assert PersonalSafeAccess.wrenchRemoves(PersonalSafeAccess.Verdict.ALLOW, true);

        assert !PersonalSafeAccess.syncViewers(1);
        assert PersonalSafeAccess.syncViewers(20);
        assert !PersonalSafeAccess.syncViewers(21);
        assert PersonalSafeAccess.syncViewers(40);

        System.out.println("PersonalSafeAccessCheck ok");
    }
}
