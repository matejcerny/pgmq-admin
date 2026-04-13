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

/* Save notify throttle */
function saveNotifyThrottle(queueName) {
  var ms = document.getElementById('throttle-ms-' + queueName).value;
  htmx.ajax('POST', '/queues/' + queueName + '/settings/notify-insert/update?throttleMs=' + ms, {target: '#notify-modal-content', swap: 'outerHTML'});
}
