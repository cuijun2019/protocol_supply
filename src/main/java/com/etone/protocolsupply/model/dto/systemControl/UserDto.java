package com.etone.protocolsupply.model.dto.systemControl;

import com.etone.protocolsupply.model.entity.user.User;
import lombok.Data;

@Data
public class UserDto extends User {
    private String token;
    private String leader;
}
