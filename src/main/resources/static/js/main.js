function showAddGathererForm(deployerId, projectId) {
    window.location.href = `/gatherer/add?deployerId=${deployerId}&projectId=${projectId}`;
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

// 删除采集配置
function deleteGatherer(gathererId) {
    if (confirm('确定要删除此采集配置吗？')) {
        fetch(`/api/gatherers/${gathererId}`, {
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

// 显示采集配置详情
function showGathererDetail(gathererId) {
    fetch(`/api/gatherers/${gathererId}`)
        .then(response => response.json())
        .then(gatherer => {
            const detailHtml = `
                <div class="modal" id="gathererDetailModal">
                    <div class="modal-content">
                        <h3>采集配置详情</h3>
                        <div class="detail-content">
                            <p><strong>采集器名称：</strong> ${gatherer.gathererName}</p>
                            <p><strong>代理路径：</strong> ${gatherer.agentPath}</p>
                            <p><strong>日志路径：</strong> ${gatherer.logPath}</p>
                            <p><strong>创建时间：</strong> ${new Date(gatherer.gatherTime).toLocaleString()}</p>
                        </div>
                        <div class="button-group">
                            <button onclick="closeGathererDetailModal()" class="btn-secondary">关闭</button>
                        </div>
                    </div>
                </div>
            `;
            document.body.insertAdjacentHTML('beforeend', detailHtml);
        })
        .catch(error => alert('获取详情失败：' + error));
}

// 关闭采集配置详情模态框
function closeGathererDetailModal() {
    const modal = document.getElementById('gathererDetailModal');
    if (modal) {
        modal.remove();
    }
} 