/* RecruitPlace — UI Prototype JS
   Static demo only. No backend calls, no fetch, no network requests.
   - Sidebar / mobile nav toggle
   - Active-nav highlighting by current page
   - Client-side table filter: input[data-filter] filters rows in its page's table
   - Demo toast on form submit (preventDefault)
*/
(function () {
  'use strict';

  /* ---- Active nav highlighting ---- */
  var path = window.location.pathname.split('/').pop() || 'index.html';
  if (path === '' || path.indexOf('.html') === -1) path = 'index.html';
  var navLinks = document.querySelectorAll('.nav a[data-page]');
  navLinks.forEach(function (a) {
    if (a.getAttribute('data-page') === path) a.classList.add('active');
    else a.classList.remove('active');
  });

  /* ---- Sidebar / mobile nav toggle ---- */
  var hamburger = document.getElementById('hamburger');
  var sidebar = document.getElementById('sidebar');
  var backdrop = document.getElementById('sidebar-backdrop');
  function closeSidebar() {
    if (!sidebar) return;
    sidebar.classList.remove('open');
    if (backdrop) backdrop.classList.add('hidden');
    document.body.style.overflow = '';
  }
  function toggleSidebar() {
    if (!sidebar) return;
    var willOpen = !sidebar.classList.contains('open');
    sidebar.classList.toggle('open', willOpen);
    if (backdrop) backdrop.classList.toggle('hidden', !willOpen);
    document.body.style.overflow = willOpen ? 'hidden' : '';
  }
  if (hamburger && sidebar) {
    hamburger.addEventListener('click', function (e) {
      e.stopPropagation();
      toggleSidebar();
    });
  }
  if (backdrop) backdrop.addEventListener('click', closeSidebar);
  // Close the drawer after tapping a nav link on mobile
  navLinks.forEach(function (a) {
    a.addEventListener('click', function () {
      if (window.innerWidth <= 720) closeSidebar();
    });
  });
  window.addEventListener('resize', function () {
    if (window.innerWidth > 720) closeSidebar();
  });

  /* ---- Client-side table filter ----
     Any <input data-filter> filters rows in the table(s) on that page.
     Optional data-filter-target="#id" limits it to one table. */
  document.querySelectorAll('input[data-filter]').forEach(function (input) {
    input.addEventListener('input', function () {
      var q = input.value.trim().toLowerCase();
      var selector = input.getAttribute('data-filter-target') || 'table tbody';
      document.querySelectorAll(selector).forEach(function (tbody) {
        tbody.querySelectorAll('tr').forEach(function (row) {
          row.style.display = q === '' || row.textContent.toLowerCase().indexOf(q) !== -1
            ? '' : 'none';
        });
      });
      // Keep chip state in sync is handled separately; filtering is text-only.
    });
  });

  /* ---- Filter chips (candidates / applications status chips) ---- */
  document.querySelectorAll('[data-chip-group]').forEach(function (group) {
    var chips = group.querySelectorAll('.chip[data-filter-value]');
    chips.forEach(function (chip) {
      chip.addEventListener('click', function () {
        chips.forEach(function (c) { c.classList.remove('active'); });
        chip.classList.add('active');
        var value = (chip.getAttribute('data-filter-value') || '').toLowerCase();
        var target = group.getAttribute('data-chip-target') || 'table tbody';
        document.querySelectorAll(target).forEach(function (tbody) {
          tbody.querySelectorAll('tr').forEach(function (row) {
            // Exact cell match (so "Java" does not also match "JavaScript").
            var show = value === 'all' || value === '';
            if (!show) {
              row.querySelectorAll('td').forEach(function (cell) {
                if (cell.textContent.trim().toLowerCase() === value) { show = true; }
              });
            }
            row.style.display = show ? '' : 'none';
          });
        });
      });
    });
  });

  /* ---- Demo toast on form submit ---- */
  var toast = document.getElementById('toast');
  var toastTimer = null;
  function showToast(message) {
    if (!toast) return;
    toast.textContent = message;
    toast.classList.add('show');
    clearTimeout(toastTimer);
    toastTimer = setTimeout(function () { toast.classList.remove('show'); }, 3200);
  }
  document.querySelectorAll('form[data-demo-form]').forEach(function (form) {
    form.addEventListener('submit', function (e) {
      e.preventDefault();
      showToast(form.getAttribute('data-toast') || 'Saved (demo only — not connected to the backend).');
      form.reset();
    });
  });

  /* Expose toast for the login demo button (login.html uses a plain link). */
  window.RecruitPlaceToast = showToast;
})();
