/* app.js — Router, state, and all user interactions */

let currentPage = '';
let currentStudentId = null;
let searchState = {};
let pageNum = 0;
let debounceTimer = null;

// ============ INIT ============
window.addEventListener('DOMContentLoaded', () => {
  const token = localStorage.getItem('token');
  if (token) {
    showSidebar();
    navigate('dashboard');
  } else {
    navigate('home');
  }
});

// ============ ROUTER ============
function navigate(page, id = null) {
  currentPage = page;
  currentStudentId = id;
  const container = document.getElementById('page-container');
  container.innerHTML = '<div class="loading"><div class="spinner"></div></div>';

  document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
  const activeNav = document.querySelector(`.nav-item[onclick*="${page}"]`);
  if (activeNav) activeNav.classList.add('active');

  switch (page) {
    case 'home': renderHome(); break;
    case 'login': renderLogin(); break;
    case 'forgot-password': document.getElementById('page-container').innerHTML = Pages.forgotPassword(); break;
    case 'verify-otp': document.getElementById('page-container').innerHTML = Pages.verifyOtp(); break;
    case 'reset-password': document.getElementById('page-container').innerHTML = Pages.resetPassword(); break;
    case 'dashboard': renderDashboard(); break;
    case 'students': pageNum = 0; renderStudents(); break;
    case 'register': renderRegisterForm(null); break;
    case 'edit': renderEditForm(id); break;
    case 'student-detail': renderStudentDetail(id); break;
    case 'subjects': renderSubjects(); break;
    case 'staff': loadStaff(); break;
    case 'signup': renderSignup(); break;
    default: navigate('home');
  }
}

async function loadStaff() {
  document.getElementById('page-container').innerHTML = Pages.staff();
  try {
    const res = await api.getStaff();
    const staff = res.data;
    const body = document.getElementById('staff-table-body');
    if (!staff || staff.length === 0) {
      body.innerHTML = '<tr><td colspan="4" style="text-align:center;padding:40px;color:var(--text-muted)">No staff members found in your school.</td></tr>';
      return;
    }
    body.innerHTML = staff.map(s => `
      <tr>
        <td>
          <div style="font-weight:500">${s.fullName}</div>
        </td>
        <td>${s.username}</td>
        <td>
          <span class="badge ${s.isApproved ? 'badge-success' : 'badge-warning'}">
            ${s.isApproved ? 'Approved' : 'Pending'}
          </span>
        </td>
        <td style="text-align:right">
          ${!s.isApproved ? `
            <button class="btn btn-sm btn-success" onclick="approveStaff(${s.id})" style="padding:4px 8px; font-size:12px">Approve</button>
          ` : ''}
          <button class="btn btn-sm btn-danger" onclick="rejectStaff(${s.id})" style="padding:4px 8px; font-size:12px">Remove</button>
        </td>
      </tr>
    `).join('');
  } catch (e) { toast(e.message, 'error'); }
}

async function approveStaff(id) {
  try {
    await api.approveStaff(id);
    toast('Teacher approved!', 'success');
    loadStaff();
  } catch (e) { toast(e.message, 'error'); }
}

async function rejectStaff(id) {
  if (!confirm('Are you sure you want to remove this teacher?')) return;
  try {
    await api.rejectStaff(id);
    toast('Teacher removed!', 'success');
    loadStaff();
  } catch (e) { toast(e.message, 'error'); }
}

// ============ AUTH ============
async function doLogin() {
  const u = document.getElementById('username').value.trim();
  const p = document.getElementById('password').value.trim();
  const errEl = document.getElementById('login-error');
  errEl.style.display = 'none';
  if (!u || !p) { showErr(errEl, 'Please enter username and password'); return; }
  try {
    const res = await api.login(u, p);
    localStorage.setItem('token', res.data.token);
    localStorage.setItem('adminName', res.data.fullName || res.data.username);
    localStorage.setItem('schoolName', res.data.schoolName || '');
    localStorage.setItem('userRole', res.data.role || 'ROLE_ADMIN');
    localStorage.setItem('schoolCode', res.data.schoolCode || '');
    localStorage.setItem('isApproved', res.data.isApproved);
    showSidebar();
    navigate('dashboard');
  } catch (e) { showErr(errEl, e.message || 'Login failed'); }
}

document.addEventListener('keydown', e => {
  if (e.key === 'Enter' && currentPage === 'login') doLogin();
});

function showErr(el, msg) { el.textContent = msg; el.style.display = 'block'; }

function logout() {
  localStorage.clear();
  hideSidebar();
  navigate('home');
}

function showSidebar() {
  document.getElementById('sidebar').classList.remove('hidden');
  document.getElementById('main-content').classList.remove('full-width');
  const name = localStorage.getItem('adminName');
  if (name) document.getElementById('admin-name').textContent = name;
  const role = localStorage.getItem('userRole');
  if (role) {
    const roleEl = document.querySelector('.admin-role');
    if (roleEl) roleEl.textContent = role.replace('ROLE_', '');
    
    // Show/hide admin only items
    document.querySelectorAll('.admin-only').forEach(el => {
      el.style.display = (role === 'ROLE_ADMIN') ? 'flex' : 'none';
    });
  }
  const schoolCode = localStorage.getItem('schoolCode');
  const scEl = document.getElementById('school-code-display');
  if (scEl) {
    scEl.textContent = schoolCode ? `Code: ${schoolCode}` : '';
    scEl.title = "Share this code with your teachers to join your school";
  }
  const schoolName = localStorage.getItem('schoolName');
  
  const brandEl = document.getElementById('brand-name-header');
  if (brandEl) {
    brandEl.textContent = 'EduTrack';
  }

  const topHeader = document.getElementById('top-header');
  const schoolNameDisplay = document.getElementById('school-name-display');
  if (topHeader && schoolNameDisplay) {
    topHeader.classList.remove('hidden');
    schoolNameDisplay.textContent = schoolName || 'EduTrack Portal';
  }
}

function hideSidebar() {
  document.getElementById('sidebar').classList.add('hidden');
  document.getElementById('main-content').classList.add('full-width');
  const topHeader = document.getElementById('top-header');
  if (topHeader) topHeader.classList.add('hidden');
}

// ============ HOME, LOGIN & SIGNUP ============
function renderHome() {
  hideSidebar();
  document.getElementById('page-container').innerHTML = Pages.home();
}

function renderLogin() {
  hideSidebar();
  document.getElementById('page-container').innerHTML = Pages.login();
}

function renderSignup() {
  hideSidebar();
  document.getElementById('page-container').innerHTML = Pages.signup();
}

function toggleSchoolCode() {
  const role = document.getElementById('su-role').value;
  const codeGroup = document.getElementById('school-code-group');
  const nameGroup = document.getElementById('school-name-group');
  if (role === 'ROLE_TEACHER') {
    codeGroup.style.display = 'block';
    nameGroup.style.display = 'none';
  } else {
    codeGroup.style.display = 'none';
    nameGroup.style.display = 'block';
  }
}

async function doSignup() {
  const e = document.getElementById('su-email').value.trim();
  const p = document.getElementById('su-password').value.trim();
  const s = document.getElementById('su-school').value.trim();
  const r = document.getElementById('su-role').value;
  const sc = document.getElementById('su-school-code').value.trim();
  const errEl = document.getElementById('signup-error');
  errEl.style.display = 'none';
  
  if (!e || !p) { showErr(errEl, 'Please fill email and password'); return; }
  if (r === 'ROLE_ADMIN' && !s) { showErr(errEl, 'School Name is required for Admins'); return; }
  if (r === 'ROLE_TEACHER' && !sc) { showErr(errEl, 'School Code is required for Teachers'); return; }

  try {
    const res = await api.register(e, p, s, r, sc);
    if (res.data && res.data.token) {
        localStorage.setItem('token', res.data.token);
        localStorage.setItem('adminName', res.data.fullName || res.data.username);
        localStorage.setItem('schoolName', res.data.schoolName || '');
        localStorage.setItem('userRole', res.data.role || 'ROLE_ADMIN');
        localStorage.setItem('schoolCode', res.data.schoolCode || '');
        localStorage.setItem('isApproved', res.data.isApproved);
        toast('Registration successful!', 'success');
        showSidebar();
        navigate('dashboard');
    } else {
        toast('Registration successful! Please login.', 'success');
        navigate('login');
    }
  } catch (err) {
    showErr(errEl, err.message || 'Registration failed');
  }
}

// ============ DASHBOARD ============
async function syncApprovalStatus() {
  try {
    const res = await api.getMe();
    localStorage.setItem('isApproved', res.data.isApproved);
  } catch (e) { console.error("Status sync failed", e); }
}

async function renderDashboard() {
  await syncApprovalStatus();
  try {
    const res = await api.getDashboardAnalytics();
    const stats = res.data || {};
    document.getElementById('page-container').innerHTML = Pages.dashboard(stats);
    
    // Render Charts
    renderCharts(stats);
  } catch (e) { 
    console.error(e);
    toast(e.message, 'error'); 
  }
}

function renderCharts(stats) {
  const streamCtx = document.getElementById('streamChart');
  const classCtx = document.getElementById('classChart');
  
  if (!streamCtx || !classCtx) return;

  // Stream Chart (Pie)
  new Chart(streamCtx, {
    type: 'pie',
    data: {
      labels: Object.keys(stats.studentsPerStream || {}),
      datasets: [{
        data: Object.values(stats.studentsPerStream || {}),
        backgroundColor: [
          '#6366f1', '#8b5cf6', '#ec4899', '#f43f5e', '#f59e0b', '#10b981', '#06b6d4'
        ],
        borderWidth: 0
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { position: 'bottom', labels: { color: '#94a3b8', font: { family: 'Inter' } } }
      }
    }
  });

  // Class Chart (Bar)
  new Chart(classCtx, {
    type: 'bar',
    data: {
      labels: Object.keys(stats.studentsPerClass || {}),
      datasets: [{
        label: 'Students',
        data: Object.values(stats.studentsPerClass || {}),
        backgroundColor: '#6366f1',
        borderRadius: 6
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false }
      },
      scales: {
        y: { 
          beginAtZero: true, 
          ticks: { color: '#94a3b8' },
          grid: { color: 'rgba(255,255,255,0.05)' }
        },
        x: { 
          ticks: { color: '#94a3b8' },
          grid: { display: false }
        }
      }
    }
  });
}

// ============ STUDENTS LIST ============
async function renderStudents() {
  try {
    const params = new URLSearchParams();
    params.append('page', pageNum);
    params.append('size', '10');
    params.append('sortBy', 'fullName');
    params.append('sortDir', 'asc');
    
    if (searchState.name) params.append('name', searchState.name);
    if (searchState.samagraId) params.append('samagraId', searchState.samagraId);
    if (searchState.admNo) params.append('admNo', searchState.admNo);
    if (searchState.className) params.append('className', searchState.className);
    if (searchState.stream) params.append('stream', searchState.stream);

    console.log('Final Params String:', params.toString());
    const res = await api.getStudents(params.toString());
    const data = res.data || {};
    document.getElementById('page-container').innerHTML = Pages.students(data, searchState);
    renderPagination(data.totalPages || 0);
  } catch (e) { toast(e.message, 'error'); }
}

function searchStudents() {
  if (debounceTimer) clearTimeout(debounceTimer);
  const nameVal = document.getElementById('s-name')?.value.trim() || '';
  const samagraVal = document.getElementById('s-samagra')?.value.trim() || '';
  const admNoVal = document.getElementById('s-admNo')?.value.trim() || '';
  
  console.log('DOM READ: admNo =', admNoVal);

  searchState = {
    name: nameVal,
    samagraId: samagraVal,
    admNo: admNoVal,
    className: document.getElementById('s-class')?.value || '',
    stream: document.getElementById('s-stream')?.value || '',
  };
  console.log('New Search State:', JSON.stringify(searchState));
  pageNum = 0;
  renderStudents();
}

function clearSearch() { searchState = {}; pageNum = 0; renderStudents(); }

function debounceSearch() {
  clearTimeout(debounceTimer);
  debounceTimer = setTimeout(searchStudents, 400);
}

function renderPagination(total) {
  const el = document.getElementById('pagination');
  if (!el || total <= 1) return;
  let html = `<button class="page-btn" onclick="goPage(${pageNum - 1})" ${pageNum === 0 ? 'disabled' : ''}>‹ Prev</button>`;
  for (let i = 0; i < total; i++) {
    if (total > 7 && Math.abs(i - pageNum) > 2 && i !== 0 && i !== total - 1) { if (Math.abs(i - pageNum) === 3) html += '<span style="color:var(--text-muted)">…</span>'; continue; }
    html += `<button class="page-btn ${i === pageNum ? 'active' : ''}" onclick="goPage(${i})">${i + 1}</button>`;
  }
  html += `<button class="page-btn" onclick="goPage(${pageNum + 1})" ${pageNum >= total - 1 ? 'disabled' : ''}>Next ›</button>`;
  el.innerHTML = html;
}

function goPage(n) { pageNum = n; renderStudents(); }

function confirmDelete(id, name) {
  document.getElementById('modal-content').innerHTML = `
    <h3 style="margin-bottom:12px">🗑 Delete Student</h3>
    <p style="color:var(--text-secondary);margin-bottom:20px">Are you sure you want to delete <strong>${name}</strong>? This cannot be undone.</p>
    <div style="display:flex;gap:10px;justify-content:flex-end">
      <button class="btn btn-secondary" onclick="closeModal()">Cancel</button>
      <button class="btn btn-danger" onclick="doDelete(${id})">Delete</button>
    </div>`;
  openModal();
}

async function doDelete(id) {
  try {
    await api.deleteStudent(id);
    closeModal();
    toast('Student deleted', 'success');
    navigate('students');
  } catch (e) { toast(e.message, 'error'); }
}

// ============ SUBJECTS ============
async function renderSubjects() {
  try {
    const res = await api.getAllSubjects();
    document.getElementById('page-container').innerHTML = Pages.subjects(res.data || []);
  } catch (e) { toast(e.message, 'error'); }
}

function showAddSubject() {
  document.getElementById('modal-content').innerHTML = `
    <h3 style="margin-bottom:16px">📚 Add Subject</h3>
    <div class="form-group"><label class="form-label">Subject Name *</label><input id="m-subName" class="form-control" placeholder="e.g. Mathematics"/></div>
    <div class="form-group"><label class="form-label">Subject Code</label><input id="m-subCode" class="form-control" placeholder="e.g. MATH"/></div>
    <div class="form-group"><label class="form-label">Description</label><input id="m-subDesc" class="form-control" placeholder="Optional"/></div>
    <div style="display:flex;gap:10px;justify-content:flex-end;margin-top:16px">
      <button class="btn btn-secondary" onclick="closeModal()">Cancel</button>
      <button class="btn btn-primary" onclick="saveSubject()">Save</button>
    </div>`;
  openModal();
}

async function saveSubject() {
  const name = document.getElementById('m-subName').value.trim();
  if (!name) { toast('Subject name is required', 'error'); return; }
  try {
    await api.createSubject({ subjectName: name, subjectCode: document.getElementById('m-subCode').value.trim(), description: document.getElementById('m-subDesc').value.trim() });
    closeModal(); toast('Subject created!', 'success'); renderSubjects();
  } catch (e) { toast(e.message, 'error'); }
}

function showEditSubject(id, name, code, desc) {
  document.getElementById('modal-content').innerHTML = `
    <h3 style="margin-bottom:16px">✏️ Edit Subject</h3>
    <div class="form-group"><label class="form-label">Subject Name *</label><input id="m-subName" class="form-control" value="${name}"/></div>
    <div class="form-group"><label class="form-label">Subject Code</label><input id="m-subCode" class="form-control" value="${code}"/></div>
    <div class="form-group"><label class="form-label">Description</label><input id="m-subDesc" class="form-control" value="${desc}"/></div>
    <div style="display:flex;gap:10px;justify-content:flex-end;margin-top:16px">
      <button class="btn btn-secondary" onclick="closeModal()">Cancel</button>
      <button class="btn btn-primary" onclick="doEditSubject(${id})">Save</button>
    </div>`;
  openModal();
}

async function doEditSubject(id) {
  const name = document.getElementById('m-subName').value.trim();
  if (!name) { toast('Subject name is required', 'error'); return; }
  try {
    await api.updateSubject(id, { subjectName: name, subjectCode: document.getElementById('m-subCode').value.trim(), description: document.getElementById('m-subDesc').value.trim() });
    closeModal(); toast('Subject updated!', 'success'); renderSubjects();
  } catch (e) { toast(e.message, 'error'); }
}

function confirmDeleteSubject(id, name) {
  document.getElementById('modal-content').innerHTML = `
    <h3 style="margin-bottom:12px">🗑 Delete Subject</h3>
    <p style="color:var(--text-secondary);margin-bottom:20px">Are you sure you want to delete <strong>${name}</strong>? This will remove it from all students who have it assigned.</p>
    <div style="display:flex;gap:10px;justify-content:flex-end">
      <button class="btn btn-secondary" onclick="closeModal()">Cancel</button>
      <button class="btn btn-danger" onclick="doDeleteSubject(${id})">Delete</button>
    </div>`;
  openModal();
}

async function doDeleteSubject(id) {
  try {
    await api.deleteSubject(id);
    closeModal(); toast('Subject deleted', 'success'); renderSubjects();
  } catch (e) { toast(e.message, 'error'); }
}

// ============ STUDENT DETAIL ============
async function renderStudentDetail(id) {
  try {
    const res = await api.getFullDetails(id);
    document.getElementById('page-container').innerHTML = Pages.studentDetail(res.data || {});
  } catch (e) { toast(e.message, 'error'); }
}

function showTab(tabId) {
  document.querySelectorAll('.tab-pane').forEach(el => el.style.display = 'none');
  document.getElementById('tab-' + tabId).style.display = 'block';
  document.querySelectorAll('#detail-tabs .tab-btn').forEach(b => b.classList.remove('active'));
  if (window.event) window.event.target.classList.add('active');
}

// Fetch doc as authenticated blob then open inline
async function viewDocument(docId, fileName) {
  toast('Loading document...', 'info');
  try {
    const token = api.getToken();
    const res = await fetch(api.downloadUrl(docId), {
      headers: { Authorization: `Bearer ${token}` }
    });
    if (!res.ok) throw new Error('Could not load document');
    const blob = await res.blob();
    const objectUrl = URL.createObjectURL(blob);
    const isImage = blob.type.startsWith('image/');
    const isPdf = blob.type === 'application/pdf';

    if (isImage) {
      // Show image in modal lightbox
      document.getElementById('modal-content').innerHTML = `
        <div style="display:flex;flex-direction:column;gap:12px">
          <div style="display:flex;justify-content:space-between;align-items:center">
            <h3 style="font-size:15px">📎 ${fileName}</h3>
            <button class="btn btn-secondary btn-sm" onclick="closeModal()">✕ Close</button>
          </div>
          <div style="text-align:center;background:rgba(0,0,0,0.3);border-radius:8px;padding:12px">
            <img src="${objectUrl}" alt="${fileName}"
              style="max-width:100%;max-height:65vh;border-radius:8px;object-fit:contain;cursor:zoom-in"
              onclick="this.style.maxHeight=this.style.maxHeight==='none'?'65vh':'none'"
              title="Click to toggle zoom"/>
          </div>
          <div style="text-align:right">
            <button class="btn btn-info btn-sm" onclick="downloadBlobUrl('${objectUrl}','${fileName}')">⬇ Download</button>
          </div>
        </div>`;
      // Widen modal for image viewing
      document.querySelector('.modal').style.maxWidth = '800px';
      openModal();
    } else if (isPdf) {
      // Open PDF in new tab
      window.open(objectUrl, '_blank');
    } else {
      // Unknown type — trigger download
      downloadBlobUrl(objectUrl, fileName);
    }
  } catch (e) { toast(e.message || 'Failed to load document', 'error'); }
}

// Programmatic download with auth
async function downloadDocument(docId, fallbackFileName) {
  toast('Preparing download...', 'info');
  try {
    const token = api.getToken();
    const res = await fetch(api.downloadUrl(docId), {
      headers: { Authorization: `Bearer ${token}` }
    });
    if (!res.ok) throw new Error('Download failed');

    // Extract real filename from backend header if available
    let finalFileName = fallbackFileName;
    const cd = res.headers.get('Content-Disposition');
    if (cd) {
      const filenameMatch = cd.match(/filename\*?=['"]?(?:UTF-8'')?([^'";]+)['"]?/i);
      if (filenameMatch && filenameMatch[1]) {
        finalFileName = decodeURIComponent(filenameMatch[1]);
      }
    }

    const blob = await res.blob();
    downloadBlobUrl(URL.createObjectURL(blob), finalFileName);
    toast('Download started', 'success');
  } catch (e) { toast(e.message || 'Download failed', 'error'); }
}

// Helper: trigger browser save dialog
function downloadBlobUrl(url, fileName) {
  const a = document.createElement('a');
  a.href = url; a.download = fileName;
  document.body.appendChild(a); a.click();
  document.body.removeChild(a);
}

async function downloadRegistrationForm(studentId) {
  toast('Generating registration form...', 'info');
  try {
    const token = api.getToken();
    const res = await fetch(api.studentRegistrationUrl(studentId), {
      headers: { Authorization: `Bearer ${token}` }
    });
    if (!res.ok) throw new Error('Generation failed');
    
    let fileName = `registration_${studentId}.pdf`;
    const cd = res.headers.get('Content-Disposition');
    if (cd) {
      const filenameMatch = cd.match(/filename\*?=['"]?(?:UTF-8'')?([^'";]+)['"]?/i);
      if (filenameMatch && filenameMatch[1]) fileName = decodeURIComponent(filenameMatch[1]);
    }

    const blob = await res.blob();
    downloadBlobUrl(URL.createObjectURL(blob), fileName);
    toast('Download started', 'success');
  } catch (e) { toast(e.message || 'Download failed', 'error'); }
}

async function exportStudentsCsv() {
  toast('Exporting students to CSV...', 'info');
  try {
    const token = api.getToken();
    const params = new URLSearchParams({
      ...(searchState.name && { name: searchState.name }),
      ...(searchState.samagraId && { samagraId: searchState.samagraId }),
      ...(searchState.admNo && { admNo: searchState.admNo }),
      ...(searchState.className && { className: searchState.className }),
      ...(searchState.stream && { stream: searchState.stream }),
    });
    const res = await fetch(api.exportCsvUrl(params.toString()), {
      headers: { Authorization: `Bearer ${token}` }
    });
    if (!res.ok) throw new Error('Export failed');

    let fileName = 'students_export.csv';
    const cd = res.headers.get('Content-Disposition');
    if (cd) {
      const filenameMatch = cd.match(/filename\*?=['"]?(?:UTF-8'')?([^'";]+)['"]?/i);
      if (filenameMatch && filenameMatch[1]) fileName = decodeURIComponent(filenameMatch[1]);
    }

    const blob = await res.blob();
    downloadBlobUrl(URL.createObjectURL(blob), fileName);
    toast('Export complete', 'success');
  } catch (e) { toast(e.message || 'Export failed', 'error'); }
}


function showUploadDoc(studentId) {
  document.getElementById('modal-content').innerHTML = `
    <h3 style="margin-bottom:16px">📤 Upload Document</h3>
    <div class="form-group"><label class="form-label">Document Type *</label>
      <select id="u-docType" class="form-control">
        ${['AADHAAR', 'SAMAGRA_ID', 'APAAR_ID', 'PEN_NUMBER', 'INCOME_CERTIFICATE', 'DOMICILE_CERTIFICATE', 'BIRTH_CERTIFICATE', 'CASTE_CERTIFICATE', 'TRANSFER_CERTIFICATE', 'ADMISSION_FORM', 'MP_TASS', 'MARKSHEET', 'STUDENT_PHOTO', 'PASSBOOK'].map(t => `<option>${t}</option>`).join('')}
      </select></div>
    <div class="form-group"><label class="form-label">Document Number</label><input id="u-docNum" class="form-control" placeholder="e.g. 1234 5678 9012"/></div>
    <div class="form-group"><label class="form-label">File (PDF/JPG/PNG)</label><input id="u-file" class="form-control" type="file" accept=".pdf,.jpg,.jpeg,.png"/></div>
    <div style="display:flex;gap:10px;justify-content:flex-end;margin-top:16px">
      <button class="btn btn-secondary" onclick="closeModal()">Cancel</button>
      <button class="btn btn-primary" onclick="doUploadDoc(${studentId})">Upload</button>
    </div>`;
  openModal();
}

async function doUploadDoc(studentId) {
  const docType = document.getElementById('u-docType').value;
  const docNum = document.getElementById('u-docNum').value;
  
  if (docType === 'PEN_NUMBER' && !/^\d{11}$/.test(docNum)) {
    toast('PEN Number must be exactly 11 digits', 'error');
    return;
  }

  const file = document.getElementById('u-file').files[0];
  const metadata = JSON.stringify({ documentType: docType, documentNumber: docNum });
  const fd = new FormData();
  fd.append('metadata', new Blob([metadata], { type: 'application/json' }));
  if (file) fd.append('file', file);
  try {
    await api.uploadDocument(studentId, fd);
    closeModal(); toast('Document uploaded!', 'success');
    renderStudentDetail(studentId);
  } catch (e) { toast(e.message || 'Upload failed', 'error'); }
}
// Update Document Status
async function updateDocStatus(docId, status, studentId) {
  try {
    const metadata = JSON.stringify({ verificationStatus: status });
    const fd = new FormData();
    fd.append('metadata', new Blob([metadata], { type: 'application/json' }));
    await api.updateDocument(docId, fd);
    toast('Document status updated', 'success');
    renderStudentDetail(studentId);
  } catch (e) { toast(e.message || 'Update failed', 'error'); }
}

// Delete Document
async function deleteDoc(docId, studentId) {
  if (!confirm('Are you sure you want to delete this document?')) return;
  try {
    await api.deleteDocument(docId);
    toast('Document deleted', 'success');
    renderStudentDetail(studentId);
  } catch (e) { toast(e.message || 'Delete failed', 'error'); }
}

function showEditDoc(docId, studentId, type, num) {
  document.getElementById('modal-content').innerHTML = `
    <h3 style="margin-bottom:16px">✏️ Edit Document</h3>
    <div class="form-group"><label class="form-label">Document Type *</label>
      <select id="e-docType" class="form-control">
        ${['AADHAAR', 'SAMAGRA_ID', 'APAAR_ID', 'PEN_NUMBER', 'INCOME_CERTIFICATE', 'DOMICILE_CERTIFICATE', 'BIRTH_CERTIFICATE', 'CASTE_CERTIFICATE', 'TRANSFER_CERTIFICATE', 'ADMISSION_FORM', 'MP_TASS', 'MARKSHEET', 'STUDENT_PHOTO', 'PASSBOOK'].map(t => `<option ${t === type ? 'selected' : ''}>${t}</option>`).join('')}
      </select></div>
    <div class="form-group"><label class="form-label">Document Number</label><input id="e-docNum" class="form-control" value="${num || ''}"/></div>
    <div class="form-group"><label class="form-label">Replace File (Optional)</label><input id="e-file" class="form-control" type="file" accept=".pdf,.jpg,.jpeg,.png"/></div>
    <div style="display:flex;gap:10px;justify-content:flex-end;margin-top:16px">
      <button class="btn btn-secondary" onclick="closeModal()">Cancel</button>
      <button class="btn btn-primary" onclick="doUpdateDoc(${docId}, ${studentId})">Update</button>
    </div>`;
  openModal();
}

async function doUpdateDoc(docId, studentId) {
  const docType = document.getElementById('e-docType').value;
  const docNum = document.getElementById('e-docNum').value;
  const file = document.getElementById('e-file').files[0];

  const metadata = JSON.stringify({ documentType: docType, documentNumber: docNum });
  const fd = new FormData();
  fd.append('metadata', new Blob([metadata], { type: 'application/json' }));
  if (file) fd.append('file', file);

  try {
    await api.updateDocument(docId, fd);
    closeModal(); toast('Document updated!', 'success');
    renderStudentDetail(studentId);
  } catch (e) { toast(e.message || 'Update failed', 'error'); }
}
// ============ REGISTER ============
async function renderRegisterForm(prefill) {
  try {
    const subRes = await api.getAllSubjects();
    document.getElementById('page-container').innerHTML = Pages.registerForm(prefill, subRes.data || []);
  } catch (e) { toast(e.message, 'error'); }
}

async function renderEditForm(id) {
  try {
    const res = await api.getFullDetails(id);
    await renderRegisterForm(res.data);
  } catch (e) { toast(e.message, 'error'); }
}

function showFormTab(tabId) {
  document.querySelectorAll('.tab-pane').forEach(el => el.style.display = 'none');
  document.getElementById(tabId).style.display = 'block';
  document.querySelectorAll('.tabs .tab-btn').forEach(b => b.classList.remove('active'));
  event.target.classList.add('active');
}


function getFormData() {
  const g = id => { const el = document.getElementById(id); return el ? el.value.trim() : ''; };
  const subjects = [...document.querySelectorAll('#selected-subjects .chip')].map(c => ({ subjectName: c.dataset.name, subjectCode: c.dataset.code }));
  return {
    personalInfo: { samagraId: g('f-samagraId'), fullName: g('f-fullName'), gender: g('f-gender'), dateOfBirth: g('f-dob'), bloodGroup: g('f-blood') || undefined, category: g('f-category') || undefined, religion: g('f-religion'), nationality: g('f-nationality'), fatherName: g('f-father'), motherName: g('f-mother'), mobileNumber: g('f-mobile'), email: g('f-email'), address: g('f-address'), city: g('f-city'), state: g('f-state'), pincode: g('f-pincode'), admissionDate: g('f-admDate') || undefined, studentStatus: g('f-status') },
    academicInfo: { className: g('f-class'), section: g('f-section'), rollNumber: g('f-roll'), admissionNumber: g('f-admNo'), board: g('f-board'), academicYear: g('f-year'), stream: g('f-stream') || undefined, previousSchool: g('f-prevSchool'), previousPercentage: g('f-prevPct') || undefined },
    bankDetails: { bankName: g('f-bankName'), branchName: g('f-branch'), ifscCode: g('f-ifsc') || undefined, accountNumber: g('f-accNo'), accountHolderName: g('f-accHolder') },
    subjects, documents: []
  };
}

function buildFormData(payload) {
  const fd = new FormData();
  fd.append('data', new Blob([JSON.stringify(payload)], { type: 'application/json' }));
  return fd;
}

async function submitRegister() {
  const payload = getFormData();
  if (!payload.personalInfo.samagraId || !payload.personalInfo.fullName || !payload.personalInfo.gender || !payload.personalInfo.dateOfBirth || !payload.personalInfo.mobileNumber) {
    toast('Please fill all required fields (Samagra ID, Name, Gender, DOB, Mobile)', 'error'); return;
  }
  try {
    const res = await api.registerStudent(buildFormData(payload));
    const studentId = res.data.id;
    // Upload passbook if selected
    await uploadPassbookIfSelected(studentId);
    toast('Student registered successfully!', 'success');
    navigate('student-detail', studentId);
  } catch (e) { toast(e.message, 'error'); }
}

async function submitUpdate() {
  const payload = getFormData();
  try {
    await api.updateStudent(currentStudentId, buildFormData(payload));
    // Upload passbook if a new one was selected
    await uploadPassbookIfSelected(currentStudentId);
    toast('Student updated successfully!', 'success');
    navigate('student-detail', currentStudentId);
  } catch (e) { toast(e.message, 'error'); }
}

// Upload passbook file if selected in the form
async function uploadPassbookIfSelected(studentId) {
  const fileInput = document.getElementById('f-passbook');
  if (!fileInput || !fileInput.files[0]) return;
  const file = fileInput.files[0];
  const metadata = JSON.stringify({ documentType: 'PASSBOOK', documentNumber: '' });
  const fd = new FormData();
  fd.append('metadata', new Blob([metadata], { type: 'application/json' }));
  fd.append('file', file);
  try {
    await api.uploadDocument(studentId, fd);
    toast('Passbook uploaded!', 'success');
  } catch (e) {
    toast('Student saved but passbook upload failed: ' + e.message, 'error');
  }
}

// Show image thumbnail preview when passbook file is selected
function showPassbookPreview(input) {
  const file = input.files[0];
  if (!file) return;
  const preview = document.getElementById('passbook-preview');
  const img = document.getElementById('passbook-img');
  const label = document.getElementById('passbook-file-label');
  label.textContent = file.name + ' (' + (file.size / 1024).toFixed(1) + ' KB)';
  if (file.type.startsWith('image/')) {
    img.style.display = 'block';
    img.src = URL.createObjectURL(file);
  } else {
    img.style.display = 'none';
  }
  preview.style.display = 'flex';
  preview.style.flexDirection = 'column';
  preview.style.alignItems = 'center';
}

// Open upload modal with a pre-selected document type
function showUploadDocType(studentId, docType) {
  document.getElementById('modal-content').innerHTML = `
    <h3 style="margin-bottom:16px">📤 Upload ${docType}</h3>
    <div class="form-group"><label class="form-label">Document Type</label>
      <input class="form-control" value="${docType}" readonly/></div>
    <div class="form-group"><label class="form-label">File (PDF / JPG / PNG)</label>
      <input id="u-file" class="form-control" type="file" accept=".pdf,.jpg,.jpeg,.png"/></div>
    <div style="display:flex;gap:10px;justify-content:flex-end;margin-top:16px">
      <button class="btn btn-secondary" onclick="closeModal()">Cancel</button>
      <button class="btn btn-primary" onclick="doUploadDocType(${studentId},'${docType}')">Upload</button>
    </div>`;
  openModal();
}

async function doUploadDocType(studentId, docType) {
  const file = document.getElementById('u-file').files[0];
  if (!file) { toast('Please select a file', 'error'); return; }
  const metadata = JSON.stringify({ documentType: docType, documentNumber: '' });
  const fd = new FormData();
  fd.append('metadata', new Blob([metadata], { type: 'application/json' }));
  fd.append('file', file);
  try {
    await api.uploadDocument(studentId, fd);
    closeModal(); toast(docType + ' uploaded!', 'success');
    renderStudentDetail(studentId);
  } catch (e) { toast(e.message || 'Upload failed', 'error'); }
}

// ============ SUBJECT CHIPS ============
function addSubjectFromPicker() {
  const sel = document.getElementById('subject-picker');
  const code = sel.value;
  const name = sel.options[sel.selectedIndex]?.dataset.name;
  if (!name) return;
  addChip(name, code);
}

function addNewSubject() {
  const name = document.getElementById('new-sub-name').value.trim();
  const code = document.getElementById('new-sub-code').value.trim();
  if (!name) { toast('Enter a subject name', 'error'); return; }
  addChip(name, code);
  document.getElementById('new-sub-name').value = '';
  document.getElementById('new-sub-code').value = '';
}

function addChip(name, code) {
  const container = document.getElementById('selected-subjects');
  if ([...container.querySelectorAll('.chip')].some(c => c.dataset.name === name)) { toast('Subject already added', 'info'); return; }
  const chip = document.createElement('div');
  chip.className = 'chip'; chip.dataset.name = name; chip.dataset.code = code || '';
  chip.innerHTML = `📚 ${name} <span class="chip-remove" onclick="removeSubject(this)">✕</span>`;
  container.appendChild(chip);
}

function removeSubject(el) { el.closest('.chip').remove(); }

// ============ MODAL ============
function openModal() { document.getElementById('modal-overlay').classList.remove('hidden'); }
function closeModal() {
  document.getElementById('modal-overlay').classList.add('hidden');
  document.querySelector('.modal').style.maxWidth = '';
}

// ============ TOAST ============
function toast(msg, type = 'info') {
  const el = document.getElementById('toast');
  if (!el) return;
  el.textContent = msg; el.className = `toast ${type}`;
  el.classList.remove('hidden');
  clearTimeout(el._timer);
  el._timer = setTimeout(() => el.classList.add('hidden'), 3500);
}

// ============ FORGOT PASSWORD ============
async function handleForgotPassword() {
  const em = document.getElementById('fp-email')?.value?.trim();
  const errEl = document.getElementById('fp-error');
  if (!em) return showErr(errEl, 'Email is required');
  const btn = document.getElementById('fp-btn');
  try {
    btn.disabled = true;
    btn.textContent = 'Sending OTP...';
    await api.forgotPassword(em);
    localStorage.setItem('resetEmail', em);
    toast('OTP sent to your email', 'success');
    navigate('verify-otp');
  } catch (err) {
    showErr(errEl, err.message || 'Failed to send OTP');
    if (btn) { btn.disabled = false; btn.textContent = 'Send OTP'; }
  }
}

async function handleVerifyOtp() {
  const otp = document.getElementById('vo-otp')?.value?.trim();
  const em = localStorage.getItem('resetEmail');
  const errEl = document.getElementById('vo-error');
  if (!otp || !em) return showErr(errEl, 'Please enter the OTP');
  const btn = document.getElementById('vo-btn');
  try {
    btn.disabled = true;
    await api.verifyOtp(em, otp);
    toast('OTP verified successfully', 'success');
    navigate('reset-password');
  } catch (err) {
    showErr(errEl, err.message || 'Invalid OTP');
    if (btn) { btn.disabled = false; }
  }
}

async function handleResetPassword() {
  const np = document.getElementById('rp-password')?.value?.trim();
  const em = localStorage.getItem('resetEmail');
  const errEl = document.getElementById('rp-error');
  if (!np || !em) return showErr(errEl, 'Please enter a new password');
  if (np.length < 6) return showErr(errEl, 'Password must be at least 6 characters');
  const btn = document.getElementById('rp-btn');
  try {
    btn.disabled = true;
    await api.resetPassword(em, np);
    localStorage.removeItem('resetEmail');
    toast('Password reset successfully. Please login.', 'success');
    navigate('login');
  } catch (err) {
    showErr(errEl, err.message || 'Reset failed');
    if (btn) { btn.disabled = false; }
  }
}
