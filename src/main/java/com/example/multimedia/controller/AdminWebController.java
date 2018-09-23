package com.example.multimedia.controller;

import com.example.multimedia.service.AdminNoticeService;
import com.example.multimedia.service.CommandHistoryService;
import com.example.multimedia.service.SmallTagsService;
import com.example.multimedia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author CookiesEason
 * 2018/09/22 14:22
 */
@Controller
public class AdminWebController {

    @Autowired
    private AdminNoticeService adminNoticeService;

    @Autowired
    private UserService userService;

    @Autowired
    private SmallTagsService smallTagsService;

    @Autowired
    private CommandHistoryService commandHistoryService;

    @RequestMapping("/adminLogin")
    public String adminLoginIndex(){
        return "login";
    }

    @RequestMapping("/admin/report")
    public String adminReportIndex(@RequestParam(defaultValue = "1") int page, Model model){
        model.addAttribute("reports",adminNoticeService.getReportNotice(page,"report"));
        return "adminReport";
    }

    @RequestMapping("/admin/users")
    public String adminUsers(@RequestParam(defaultValue = "1") int page, Model model){
        model.addAttribute("users",userService.findUsers(page));
        return "adminUser";
    }

    @RequestMapping("/admin/users/search")
    public String adminUsersSearch(@RequestParam String searchContent, Model model){
        model.addAttribute("users",userService.findByUsernameOrUserInfoNickname(searchContent));
        return "adminUser";
    }

    @RequestMapping("/admin/labels")
    public String adminLabels(Model model){
        model.addAttribute("labels",smallTagsService.findAll(1));
        return "adminLabel";
    }

    @RequestMapping("/admin/records")
    public String adminRecords(@RequestParam(defaultValue = "1") int page,Model model){
        model.addAttribute("records",commandHistoryService.findAll(page,16));
        return "adminRecord";
    }

}
