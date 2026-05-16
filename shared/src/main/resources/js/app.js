/* Theme: apply saved preference immediately */
var saved = localStorage.getItem('theme');
if (saved) {
  document.documentElement.setAttribute('data-theme', saved);
}

function toggleTheme() {
  var html = document.documentElement;
  var current = html.getAttribute('data-theme');
  var next = current === 'dark' ? 'light' : 'dark';
  html.setAttribute('data-theme', next);
  localStorage.setItem('theme', next);
}

/* Sidebar */
function initSidebar() {
  var sidebarOpen = localStorage.getItem('sidebarOpen') !== 'false';
  if (!sidebarOpen) {
    document.body.classList.add('sidebar-collapsed');
  }
}

function toggleSidebar() {
  document.body.classList.toggle('sidebar-collapsed');
  var isOpen = !document.body.classList.contains('sidebar-collapsed');
  localStorage.setItem('sidebarOpen', isOpen);
}

/* Create queue modal */
function createQueue() {
  var name = document.getElementById('create-queue-name').value.trim();
  if (name) {
    htmx.ajax('POST', '/queues/' + encodeURIComponent(name), {target: '#queue-table-container', swap: 'innerHTML'});
    this.closest('dialog').close();
    document.getElementById('create-queue-name').value = '';
  }
}

/* Create binding modal */
function createBinding() {
  var pattern = document.getElementById('create-binding-pattern').value.trim();
  var queueName = document.getElementById('create-binding-queue-name').value.trim();
  if (pattern && queueName) {
    htmx.ajax('POST', '/topics/bind?pattern=' + encodeURIComponent(pattern) + '&queueName=' + encodeURIComponent(queueName), {target: '#bindings-table-container', swap: 'innerHTML'});
    this.closest('dialog').close();
    document.getElementById('create-binding-pattern').value = '';
    document.getElementById('create-binding-queue-name').value = '';
  }
}

/* Test routing */
function testRouting() {
  var routingKey = document.getElementById('test-routing-key').value.trim();
  if (routingKey) {
    htmx.ajax('GET', '/topics/test-routing?routingKey=' + encodeURIComponent(routingKey), {target: '#test-routing-results', swap: 'innerHTML'});
  }
}

/* Save notify throttle */
function saveNotifyThrottle(queueName) {
  var ms = document.getElementById('throttle-ms-' + queueName).value;
  htmx.ajax('POST', '/queues/' + queueName + '/settings/notify-insert/update?throttleMs=' + ms, {target: '#notify-modal-content', swap: 'outerHTML'});
}
