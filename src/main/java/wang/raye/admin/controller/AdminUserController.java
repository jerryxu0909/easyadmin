package wang.raye.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import wang.raye.admin.model.AdminUser;
import wang.raye.admin.model.WebResult;
import wang.raye.admin.service.AdminUserService;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * 用户相关控制器
 * Created by Raye on 2017/3/21.
 */
@Controller
@RequestMapping("admin/user")
public class AdminUserController {

    @Autowired
    private AdminUserService userService;

    /**
     * 进入用户列表页面
     * @param page
     * @param pageSize
     * @param query
     * @param map
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10")int pageSize,
                        @RequestParam(defaultValue = "")String query, ModelMap map){
        List<AdminUser> users = userService.select(page,pageSize,query);
        map.put("users",users);
        int count = userService.selectCount(query);
        map.put("count",count);
        map.put("maxPage",count/10);
        map.put("page",page);
        map.put("pageSize",pageSize);
        map.put("query",query);
        map.put("nowBegin",pageSize * (page - 1 )+1);
        map.put("nowEnd",pageSize * (page - 1 )+users.size());
        return "user/index";
    }

    /**
     * 进入编辑页面
     * @param id
     * @param map
     * @return
     */
    @RequestMapping(value = "edit",method = RequestMethod.GET)
    public String edit(@RequestParam(defaultValue = "0") int id, ModelMap map){
        if(id != 0){
            map.put("user", userService.selectById(id));
        }
        return "user/edit";
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @RequestMapping(value = "edit",method = RequestMethod.POST)
    @ResponseBody
    public WebResult edit(AdminUser user, HttpSession session){
        boolean success = false;
        AdminUser loginUser = (AdminUser) session.getAttribute("loginUser");

        if(user.getId() != null){
            user.setUpdateUser(loginUser.getId());
            user.setUpdateTime(new Date());

            success = userService.update(user);
        }else {
            user.setCreator(loginUser.getId());
            success = userService.insert(user);
        }
        if(success){
            return WebResult.success();
        }else{
            return WebResult.unKnown();
        }
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @RequestMapping(value = "delete",method = RequestMethod.POST)
    @ResponseBody
    public WebResult delete(int id){
        boolean success = userService.delete(id);
        if(success){
            return WebResult.success();
        }else{
            return WebResult.unKnown();
        }
    }

    /**
     * 进入用户角色页面
     * @param id
     * @param map
     * @return
     */
    @RequestMapping(value = "role",method = RequestMethod.GET)
    public String role(int id,ModelMap map){
        map.put("roles",userService.selectUserRole(id));
        map.put("userid",id);
        return "user/role";
    }

    /**
     * 更新用户角色
     * @param ids
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "role",method = RequestMethod.POST)
    public WebResult role(String ids,int userId,HttpSession session){
        AdminUser loginUser = (AdminUser) session.getAttribute("loginUser");
        if(userService.updateRoleMenu(ids,userId,loginUser.getId())){
            return WebResult.success();
        }
        return WebResult.unKnown();
    }

    /**
     * 进入密码修改页面
     * @return
     */
    @RequestMapping(value = "updatepass",method = RequestMethod.GET)
    public String updatePass(){
        return "user/password";
    }

    /**
     * 修改密码
     * @param psw
     * @param old
     * @param session
     * @return
     */
    @RequestMapping(value = "updatepass",method = RequestMethod.POST)
    @ResponseBody
    public WebResult updatePass(String psw,String old,HttpSession session){
        AdminUser loginUser = (AdminUser) session.getAttribute("loginUser");
        return userService.updatePass(loginUser.getId(),psw,old);
    }

}
