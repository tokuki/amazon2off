package jp.co.amazon2off.security.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import java.io.Serializable;
import java.util.UUID;

@Slf4j
public class SessionIdGeneratorConfig implements SessionIdGenerator {
    @Override
    public Serializable generateId(Session session) {
        return UUID.randomUUID().toString();
    }
}
