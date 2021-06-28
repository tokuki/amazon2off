package jp.co.amazon2off.security.config;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * session监听
 */
public class ShiroSessionListener implements SessionListener {

    /**
     * 统计在线人数
     */
    private final AtomicInteger sessionCount = new AtomicInteger(0);

    @Override
    public void onStart(Session session) {
        sessionCount.incrementAndGet();
    }

    @Override
    public void onStop(Session session) {
        sessionCount.decrementAndGet();
    }

    @Override
    public void onExpiration(Session session) {
        sessionCount.decrementAndGet();
    }

    /**
     * 获取在线人数
     *
     * @return
     */
    public AtomicInteger getSessionCount() {
        return sessionCount;
    }
}
