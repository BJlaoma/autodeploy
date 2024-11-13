// 全局变量
let currentUser = null;

// 切换登录/注册表单
function toggleForm() {
    const forms = document.querySelectorAll('.auth-form');
    forms.forEach(form => {
        form.style.display = form.style.display === 'none' ? 'block' : 'none';
    });
}

// 登录处理
document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('/api/user/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            const user = await response.json();
            currentUser = user;
            showMainContainer();
            loadProjects();
        } else {
            const error = await response.json();
            alert(error.error);
        }
    } catch (error) {
        alert('登录失败：' + error.message);
    }
});

// 注册处理
document.getElementById('register-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('reg-username').value;
    const password = document.getElementById('reg-password').value;
    const email = document.getElementById('reg-email').value;

    try {
        const response = await fetch('/api/user/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password, email })
        });

        if (response.ok) {
            alert('注册成功，请登录');
            toggleForm();
        } else {
            const error = await response.json();
            alert(error.error);
        }
    } catch (error) {
        alert('注册失败：' + error.message);
    }
});

// 显示主界面
function showMainContainer() {
    document.getElementById('auth-container').style.display = 'none';
    document.getElementById('main-container').style.display = 'flex';
}

// 加载项目列表
async function loadProjects() {
    try {
        const response = await fetch(`/api/projects/user/${currentUser.uuid}`);
        if (response.ok) {
            const projects = await response.json();
            const projectList = document.getElementById('project-list');
            projectList.innerHTML = projects.map(project => `
                <div class="project-item" onclick="selectProject('${project.projectId}')">
                    ${project.projectName}
                </div>
            `).join('');
        }
    } catch (error) {
        alert('加载项目失败：' + error.message);
    }
}

// 选择项目
async function selectProject(projectId) {
    try {
        // 加载部署配置
        const deployResponse = await fetch(`/api/deployers/project/${projectId}`);
        if (deployResponse.ok) {
            const deployers = await deployResponse.json();
            const deployerList = document.getElementById('deployer-list');
            deployerList.innerHTML = deployers.map(deployer => `
                <div class="deployer-item">
                    <h4>${deployer.appName}</h4>
                    <p>部署目录: ${deployer.deployDir}</p>
                    <div class="actions">
                        <button onclick="deploy('${deployer.deployerId}')">部署</button>
                        <button onclick="deleteDeployer('${deployer.deployerId}')">删除</button>
                    </div>
                </div>
            `).join('');
        }

        // TODO: 加载采集配置（等API完成后实现）
    } catch (error) {
        alert('加载配置失败：' + error.message);
    }
}

// 显示添加部署配置表单
function showAddDeployerForm() {
    // TODO: 实现添加部署配置的弹窗表单
}

// 显示添加采集配置表单
function showAddGathererForm() {
    // TODO: 实现添加采集配置的弹窗表单
}

// 部署操作
function deploy(deployerId) {
    if (confirm('确定要执行部署吗？')) {
        fetch(`/api/deployers/${deployerId}/deploy`, {
            method: 'POST'
        })
        .then(response => {
            if (response.ok) {
                alert('部署命令已发送');
            } else {
                alert('部署失败');
            }
        })
        .catch(error => alert('部署失败：' + error));
    }
}

// 删除部署配置
function deleteDeployer(deployerId) {
    if (confirm('确定要删除此部署配置吗？')) {
        fetch(`/api/deployers/${deployerId}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                alert('删除失败');
            }
        })
        .catch(error => alert('删除失败：' + error));
    }
}

// 测试服务器连接
function testConnection(button) {
    const serverId = button.getAttribute('data-server-id');
    const originalText = button.textContent;
    button.disabled = true;
    button.textContent = '测试中...';

    fetch(`/api/servers/${serverId}/test`, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        if (data.message) {
            alert('连接成功: ' + data.message);
        } else if (data.error) {
            alert('连接失败: ' + data.error);
        }
    })
    .catch(error => {
        alert('测试连接失败：' + error);
    })
    .finally(() => {
        button.disabled = false;
        button.textContent = originalText;
    });
}

// 删除服务器
function deleteServer(button) {
    const serverId = button.getAttribute('data-server-id');
    if (confirm('确定要删除此服务器吗？')) {
        fetch(`/api/servers/${serverId}/delete`, {
            method: 'POST'
        })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                alert('删除失败');
            }
        })
        .catch(error => alert('删除失败：' + error));
    }
}

// 显示添加服务器表单
function showAddServerForm() {
    const formHtml = `
        <div class="modal" id="addServerModal">
            <div class="modal-content">
                <h3>添加服务器</h3>
                <form id="addServerForm">
                    <div class="form-group">
                        <label>IP地址</label>
                        <input type="text" id="ipAddress" required>
                    </div>
                    <div class="form-group">
                        <label>端口</label>
                        <input type="text" id="port" value="22" required>
                    </div>
                    <div class="form-group">
                        <label>用户名</label>
                        <input type="text" id="username" required>
                    </div>
                    <div class="form-group">
                        <label>密码</label>
                        <input type="password" id="password" required>
                    </div>
                    <div class="form-group">
                        <label>采集路径</label>
                        <input type="text" id="gathererPath">
                    </div>
                    <div class="button-group">
                        <button type="button" onclick="testDirectConnection()">测试连接</button>
                        <button type="submit">添加</button>
                        <button type="button" onclick="closeAddServerModal()">取消</button>
                    </div>
                </form>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', formHtml);
    
    // 添加表单提交处理
    document.getElementById('addServerForm').addEventListener('submit', function(e) {
        e.preventDefault();
        addServer();
    });

    // 添加样式
    const style = document.createElement('style');
    style.textContent = `
        .modal {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.5);
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .modal-content {
            background: white;
            padding: 20px;
            border-radius: 8px;
            width: 400px;
        }
    `;
    document.head.appendChild(style);
}

// 测试直接连接（不保存）
function testDirectConnection() {
    const data = {
        ipAddress: document.getElementById('ipAddress').value,
        port: document.getElementById('port').value,
        username: document.getElementById('username').value,
        password: document.getElementById('password').value
    };

    fetch('/api/servers/test-connection', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(data => {
        if (data.message) {
            alert('连接成功: ' + data.message);
        } else if (data.error) {
            alert('连接失败: ' + data.error);
        }
    })
    .catch(error => alert('测试连接失败：' + error));
}

// 添加服务器
function addServer() {
    const data = {
        ipAddress: document.getElementById('ipAddress').value,
        port: document.getElementById('port').value,
        username: document.getElementById('username').value,
        password: document.getElementById('password').value,
        gathererPath: document.getElementById('gathererPath').value
    };

    fetch('/api/servers', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (response.ok) {
            closeAddServerModal();
            location.reload();
        } else {
            alert('添加服务器失败');
        }
    })
    .catch(error => alert('添加服务器失败：' + error));
}

// 关闭添加服务器模态框
function closeAddServerModal() {
    const modal = document.getElementById('addServerModal');
    if (modal) {
        modal.remove();
    }
} 