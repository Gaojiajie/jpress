package io.jpress.web.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import io.jboot.utils.EncryptCookieUtils;
import io.jboot.utils.StrUtils;
import io.jboot.web.JbootControllerContext;
import io.jpress.JPressConsts;
import io.jpress.model.User;
import io.jpress.service.UserService;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用户信息的拦截器
 * @Package io.jpress.web
 */
public class UserInterceptor implements Interceptor {

    @Inject
    private UserService userService;

    public static User getThreadLocalUser() {
        return JbootControllerContext.get().getAttr(JPressConsts.ATTR_LOGINED_USER);
    }

    @Override
    public void intercept(Invocation inv) {

        Controller controller = inv.getController();
        User user = controller.getAttr(JPressConsts.ATTR_LOGINED_USER);

        if (user != null) {
            inv.invoke();
            return;
        }


        String uid = EncryptCookieUtils.get(inv.getController(), JPressConsts.COOKIE_UID);
        if (StrUtils.isBlank(uid)) {
            inv.invoke();
            return;
        }

        user = userService.findById(uid);
        if (user != null) {
            inv.getController().setAttr(JPressConsts.ATTR_LOGINED_USER, user);
        }

        inv.invoke();
    }

}
