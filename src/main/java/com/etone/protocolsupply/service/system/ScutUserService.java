package com.etone.protocolsupply.service.system;

import com.etone.protocolsupply.model.entity.user.ScutUser;
import com.etone.protocolsupply.repository.user.ScutUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
@Service
public class ScutUserService {
    private static final Logger logger = LoggerFactory.getLogger(ScutUserService.class);

    @Autowired
    private ScutUserRepository scutUserRepository;

    public ScutUser findScutUserbyXM(String scutName){
        ScutUser scutUser = scutUserRepository.findScutUserbyXM(scutName);
        if(scutUser!=null){
            return scutUser;
        }
        return null;
    }
}
