package com.autodeploy.demo.controller;

import com.autodeploy.demo.entity.Deployer;
import com.autodeploy.demo.entity.Server;
import com.autodeploy.demo.entity.User;
import com.autodeploy.demo.service.UserService;
import com.autodeploy.demo.service.ProjectService;
import com.autodeploy.demo.service.DeployerService;
import com.autodeploy.demo.service.ServerService;
import com.autodeploy.demo.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class WebController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private DeployerService deployerService;

    @Autowired
    private ServerService serverService;

    @Autowired
    private FileService fileService;

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        logger.info("访问首页");
        User user = (User) session.getAttribute("user");
        if (user == null) {
            logger.info("用户未登录，重定向到登录页");
            return "redirect:/auth/login";
        }
        
        try {
            logger.info("加载用户{}的项目列表", user.getUsername());
            model.addAttribute("projects", projectService.getUserProjects(user.getUuid()));
            
            model.addAttribute("servers", serverService.findServersByUserUuid(user.getUuid()));
        } catch (Exception e) {
            logger.error("加载数据失败", e);
            model.addAttribute("error", "加载数据失败: " + e.getMessage());
        }
        return "index";
    }

    @GetMapping("/auth/login")
    public String loginPage() {
        logger.info("访问登录页");
        return "login";
    }

    @GetMapping("/auth/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/auth/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password, 
                       HttpSession session,
                       Model model) {
        logger.info("用户尝试登录: {}", username);
        try {
            User user = userService.login(username, password);
            logger.info("用户{}登录成功", username);
            session.setAttribute("user", user);
            return "redirect:/";
        } catch (Exception e) {
            logger.error("用户{}登录失败", username, e);
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @PostMapping("/auth/register")
    public String register(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String email,
                         Model model) {
        try {
            userService.register(username, password, email);
            return "redirect:/auth/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/project/{id}")
    public String projectDetail(@PathVariable String id, Model model, HttpSession session) {
        logger.info("访问项目详情，项目ID: {}", id);
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        try {
            // 加载项目列表
            model.addAttribute("projects", projectService.getUserProjects(user.getUuid()));
            
            // 加载部署配置
            logger.info("加载项目{}的部署配置", id);
            model.addAttribute("deployers", deployerService.getProjectDeployers(id));
            model.addAttribute("projectId", id);
            
            // 加载用户的服务器列表
            model.addAttribute("servers", serverService.findServersByUserUuid(user.getUuid()));
            
            return "index";
        } catch (Exception e) {
            logger.error("加载项目详情失败", e);
            model.addAttribute("error", "加载项目详情失败: " + e.getMessage());
            return "index";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }

    @GetMapping("/deployer/add")
    public String showAddDeployerForm(@RequestParam String projectId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        // 获取当前用户的服务器列表
        model.addAttribute("servers", serverService.findServersByUserUuid(user.getUuid()));
        // 直接使用请求参数中的projectId
        model.addAttribute("projectId", projectId);
        
        return "deployer-add";
    }

    @PostMapping("/deployer/add")
    public String addDeployer(@RequestParam String projectId,
                         @RequestParam String appName,
                         @RequestParam String deployDir,
                         @RequestParam(required = false) String shPath,
                         @RequestParam String serverId,
                         @RequestParam(required = false) MultipartFile appFile,
                         @RequestParam(required = false) MultipartFile shFile,
                         HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        try {
            // 创建部署配置
            Deployer deployer = deployerService.createDeployer(projectId, appName, deployDir, shPath, serverId);
            
            // 如果有文件上传，处理文件上传
            if (appFile != null && !appFile.isEmpty()) {
                fileService.uploadFileToServer(serverId, appFile, deployDir);
            }
            if (shFile != null && !shFile.isEmpty()) {
                fileService.uploadFileToServer(serverId, shFile, shPath);
            }
            
            return "redirect:/project/" + projectId;
        } catch (Exception e) {
            // 处理错误
            return "redirect:/deployer/add?error=" + e.getMessage();
        }
    }

    @GetMapping("/deployer/{id}")
    public String showDeployerDetail(@PathVariable String id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        try {
            // 获取部署配置详情
            Deployer deployer = deployerService.getDeployerById(id);
            if (deployer == null) {
                throw new RuntimeException("部署配置不存在");
            }
            
            // 获取当前关联的服务器信息
            List<Server> servers = deployer.getServerIds().stream()
                .map(serverId -> serverService.findServerById(serverId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            
            // 获取可用的服务器列表（用户的所有服务器中排除已关联的）
            List<Server> allServers = serverService.findServersByUserUuid(user.getUuid());
            List<Server> availableServers = allServers.stream()
                .filter(server -> !deployer.getServerIds().contains(server.getServerId()))
                .collect(Collectors.toList());
            
            model.addAttribute("deployer", deployer);
            model.addAttribute("servers", servers);
            model.addAttribute("availableServers", availableServers);
            
            return "deployer-detail";
        } catch (Exception e) {
            model.addAttribute("error", "加载部署配置详情失败: " + e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/project/add")
    public String showAddProjectForm(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        return "project-add";
    }

    @PostMapping("/project/add")
    public String addProject(@RequestParam String projectName,
                            @RequestParam(required = false) String appName,
                            @RequestParam(required = false) String vision,
                            @RequestParam String message,
                            HttpSession session,
                            Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        try {
            if (appName != null && vision != null) {
                projectService.createProjectWithDetails(user.getUuid(), projectName, appName, vision, message);
            } else {
                projectService.createProject(user.getUuid(), projectName, message);
            }
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "project-add";
        }
    }
} 