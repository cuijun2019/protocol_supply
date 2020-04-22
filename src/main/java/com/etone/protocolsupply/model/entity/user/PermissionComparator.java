package com.etone.protocolsupply.model.entity.user;

import java.util.Comparator;

public class PermissionComparator implements Comparator<Permissions> {

    @Override
    public int compare(Permissions o1, Permissions o2) {
        return Integer.parseInt(o1.getPermId()+"")- Integer.parseInt(o2.getPermId()+"");
    }
}
