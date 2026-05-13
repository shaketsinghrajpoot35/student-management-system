/* ============================================
   APPLICATION LOGIC — SmartStudent Admin Portal
   ============================================ */

let currentPage = 'login';
let currentStudentId = null;
let pageNum = 0;
let pageSize = 10;
let currentSearch = {};

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
    case 'forget-password': renderForgetPassword(); break;
    case 'login': renderLogin(); break;
    case 'dashboard': renderDashboard(); break;
    case 'students': pageNum = 0; renderStudents(); break;
    case 'register': renderRegisterForm(null); break;
    case 'edit': renderEditForm(id); break;
    case 'student-detail': renderStudentDetail(id); break;
    case 'subjects': renderSubjects(); break;
    case 'signup': renderSignup(); break;
    default: navigate('dashboard');
  }
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
    showSidebar();
    navigate('dashboard');
  } catch (e) { showErr(errEl, e.message || 'Login failed'); }
}

async function doSignup() {
  const email = document.getElementById('su-email').value.trim();
  const pass = document.getElementById('su-password').value.trim();
  const school = document.getElementById('su-school').value.trim();
  const errEl = document.getElementById('signup-error');
  errEl.style.display = 'none';
  if (!email || !pass || !school) { showErr(errEl, 'All fields are required'); return; }
  try {
    await api.registerAdmin(email, pass, school);
    toast('Account created! Please login.', 'success');
    navigate('login');
  } catch (e) { showErr(errEl, e.message || 'Signup failed'); }
}

document.addEventListener('keydown', e => {
  if (e.key === 'Enter' && currentPage === 'login') doLogin();
});

function showErr(el, msg) { el.textContent = msg; el.style.display = 'block'; }

function logout() {
  localStorage.clear();
  hideSidebar();
  navigate('login');
}

function showSidebar() {
  document.getElementById('sidebar').classList.remove('hidden');
  document.getElementById('main-content').classList.remove('full-width');
  const name = localStorage.getItem('adminName');
  if (name) document.getElementById('admin-name').textContent = name;
  const schoolName = localStorage.getItem('schoolName');
  const brandEl = document.getElementById('brand-name-header');
  if (brandEl) {
    brandEl.textContent = schoolName ? schoolName : 'SmartStudent';
  }
}

function hideSidebar() {
  document.getElementById('sidebar').classList.add('hidden');
  document.getElementById('main-content').classList.add('full-width');
}

// ============ LOGIN, SIGNUP, HOME & RESET ============
function renderHome() {
  hideSidebar();
  document.getElementById('page-container').innerHTML = Pages.home();
}

function renderForgetPassword() {
  hideSidebar();
  document.getElementById('page-container').innerHTML = Pages.forgetPassword();
}

async function doChangePassword() {
  const oldP = document.getElementById('cp-old').value;
  const newP = document.getElementById('cp-new').value;
  const confP = document.getElementById('cp-confirm').value;
  const errEl = document.getElementById('cp-error');
  
  if (errEl) errEl.style.display = 'none';
  if (!oldP || !newP || !confP) return showErr(errEl, 'All fields are required');
  if (newP !== confP) return showErr(errEl, 'Passwords do not match');

  try {
    toast('Password updated successfully! Please login again.', 'success');
    navigate('login');
  } catch (e) {
    showErr(errEl, e.message || 'Failed to change password');
  }
}

function renderLogin() {
  hideSidebar();
  document.getElementById('page-container').innerHTML = Pages.login();
}

function renderSignup() {
  hideSidebar();
  document.getElementById('page-container').innerHTML = Pages.signup();
}

// ============ DASHBOARD ============
async function renderDashboard() {
  try {
    const res = await api.getStats();
    document.getElementById('page-container').innerHTML = Pages.dashboard(res.data);
    loadRecentStudents();
  } catch (e) { toast(e.message, 'error'); }
}

async function loadRecentStudents() {
  try {
    const res = await api.getStudents(0, 5, {});
    const list = res.data.content || [];
    const html = list.length === 0 ? '<p style="font-size:13px;color:var(--text-muted)">No students yet.</p>' :
      list.map(s => `
      <div style="display:flex;align-items:center;justify-content:space-between;padding:10px 0;border-bottom:1px solid var(--border)">
        <div><div style="font-size:13px;font-weight:600">${s.fullName}</div><div style="font-size:11px;color:var(--text-muted)">${s.samagraId}</div></div>
        <button class="btn btn-secondary btn-sm" onclick="navigate('student-detail',${s.id})">👁</button>
      </div>`).join('');
    document.getElementById('recent-list').innerHTML = html;
  } catch (e) { console.error(e); }
}

// ============ STUDENTS LIST ============
async function renderStudents() {
  try {
    const res = await api.getStudents(pageNum, pageSize, currentSearch);
    document.getElementById('page-container').innerHTML = Pages.students(res.data, currentSearch);
    renderPagination(res.data);
  } catch (e) { toast(e.message, 'error'); }
}

function renderPagination(data) {
  const el = document.getElementById('pagination');
  if (!el) return;
  let html = `<button class="page-btn" ${data.first ? 'disabled' : ''} onclick="changePage(${pageNum - 1})">Prev</button>`;
  html += `<span style="font-size:13px;color:var(--text-muted)">Page ${pageNum + 1} of ${data.totalPages || 1}</span>`;
  html += `<button class="page-btn" ${data.last ? 'disabled' : ''} onclick="changePage(${pageNum + 1})">Next</button>`;
  el.innerHTML = html;
}

function changePage(p) { pageNum = p; renderStudents(); }

function searchStudents() {
  currentSearch = {
    name: document.getElementById('s-name').value.trim(),
    samagraId: document.getElementById('s-samagra').value.trim(),
    className: document.getElementById('s-class').value.trim(),
    stream: document.getElementById('s-stream').value
  };
  pageNum = 0;
  renderStudents();
}

function clearSearch() {
  currentSearch = {};
  pageNum = 0;
  renderStudents();
}

let searchTimer;
function debounceSearch() {
  clearTimeout(searchTimer);
  searchTimer = setTimeout(() => searchStudents(), 500);
}

function confirmDelete(id, name) {
  if (confirm(`Are you sure you want to delete student "${name}"?`)) {
    doDelete(id);
  }
}

async function doDelete(id) {
  try {
    await api.deleteStudent(id);
    toast('Student deleted successfully', 'success');
    renderStudents();
  } catch (e) { toast(e.message, 'error'); }
}

// ============ STUDENT DETAIL ============
async function renderStudentDetail(id) {
  try {
    const res = await api.getStudentDetail(id);
    document.getElementById('page-container').innerHTML = Pages.studentDetail(res.data);
  } catch (e) { toast(e.message, 'error'); }
}

function showTab(tabId) {
  document.querySelectorAll('.tab-pane').forEach(p => p.style.display = 'none');
  document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
  document.getElementById('tab-' + tabId).style.display = 'block';
  event.currentTarget.classList.add('active');
}

// ============ SUBJECTS ============
async function renderSubjects() {
  try {
    const res = await api.getSubjects();
    document.getElementById('page-container').innerHTML = Pages.subjects(res.data);
  } catch (e) { toast(e.message, 'error'); }
}

function showAddSubject() {
  document.getElementById('modal-content').innerHTML = `
    <h3 style="margin-bottom:16px">📚 Add Subject</h3>
    <div class="form-group"><label class="form-label">Subject Name *</label><input id="m-sub-name" class="form-control"/></div>
    <div class="form-group"><label class="form-label">Subject Code</label><input id="m-sub-code" class="form-control"/></div>
    <div class="form-group"><label class="form-label">Description</label><textarea id="m-sub-desc" class="form-control"></textarea></div>
    <div style="display:flex;gap:10px;justify-content:flex-end;margin-top:16px">
      <button class="btn btn-secondary" onclick="closeModal()">Cancel</button>
      <button class="btn btn-primary" onclick="submitSubject()">Add Subject</button>
    </div>`;
  openModal();
}

async function submitSubject() {
  const n = document.getElementById('m-sub-name').value.trim();
  const c = document.getElementById('m-sub-code').value.trim();
  const d = document.getElementById('m-sub-desc').value.trim();
  if (!n) return toast('Name is required', 'error');
  try {
    await api.saveSubject({ subjectName: n, subjectCode: c, description: d });
    toast('Subject saved', 'success');
    closeModal();
    renderSubjects();
  } catch (e) { toast(e.message, 'error'); }
}

function showEditSubject(id, name, code, desc) {
  document.getElementById('modal-content').innerHTML = `
    <h3 style="margin-bottom:16px">✏️ Edit Subject</h3>
    <div class="form-group"><label class="form-label">Subject Name *</label><input id="m-sub-name" class="form-control" value="${name}"/></div>
    <div class="form-group"><label class="form-label">Subject Code</label><input id="m-sub-code" class="form-control" value="${code}"/></div>
    <div class="form-group"><label class="form-label">Description</label><textarea id="m-sub-desc" class="form-control">${desc}</textarea></div>
    <div style="display:flex;gap:10px;justify-content:flex-end;margin-top:16px">
      <button class="btn btn-secondary" onclick="closeModal()">Cancel</button>
      <button class="btn btn-primary" onclick="updateSubject(${id})">Update Subject</button>
    </div>`;
  openModal();
}

async function updateSubject(id) {
  const n = document.getElementById('m-sub-name').value.trim();
  const c = document.getElementById('m-sub-code').value.trim();
  const d = document.getElementById('m-sub-desc').value.trim();
  if (!n) return toast('Name is required', 'error');
  try {
    await api.updateSubject(id, { subjectName: n, subjectCode: c, description: d });
    toast('Subject updated', 'success');
    closeModal();
    renderSubjects();
  } catch (e) { toast(e.message, 'error'); }
}

function confirmDeleteSubject(id, name) {
  if (confirm(`Delete subject "${name}"?`)) {
    doDeleteSubject(id);
  }
}

async function doDeleteSubject(id) {
  try {
    await api.deleteSubject(id);
    toast('Subject deleted', 'success');
    renderSubjects();
  } catch (e) { toast(e.message, 'error'); }
}

// ============ FORMS ============
async function renderRegisterForm(prefill = null) {
  try {
    const res = await api.getSubjects();
    document.getElementById('page-container').innerHTML = Pages.registerForm(prefill, res.data);
  } catch (e) { toast(e.message, 'error'); }
}

async function renderEditForm(id) {
  try {
    const res = await api.getStudentDetail(id);
    const subRes = await api.getSubjects();
    document.getElementById('page-container').innerHTML = Pages.registerForm(res.data, subRes.data);
  } catch (e) { toast(e.message, 'error'); }
}

function showFormTab(tabId) {
  document.querySelectorAll('.tab-pane').forEach(p => p.style.display = 'none');
  document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
  document.getElementById(tabId).style.display = 'block';
  event.currentTarget.classList.add('active');
}

function addSubjectFromPicker() {
  const picker = document.getElementById('subject-picker');
  const code = picker.value;
  if (!code) return;
  const name = picker.options[picker.selectedIndex].getAttribute('data-name');
  addSubjectChip(name, code);
}

function addNewSubject() {
  const name = document.getElementById('new-sub-name').value.trim();
  const code = document.getElementById('new-sub-code').value.trim();
  if (!name) return;
  addSubjectChip(name, code);
  document.getElementById('new-sub-name').value = '';
  document.getElementById('new-sub-code').value = '';
}

function addSubjectChip(name, code) {
  const container = document.getElementById('selected-subjects');
  if (container.querySelector(`[data-code="${code}"]`)) return;
  const chip = document.createElement('div');
  chip.className = 'chip';
  chip.setAttribute('data-code', code);
  chip.setAttribute('data-name', name);
  chip.innerHTML = `📚 ${name} <span class="chip-remove" onclick="removeSubject(this)">✕</span>`;
  container.appendChild(chip);
}

function removeSubject(el) {
  el.parentElement.remove();
}

function showPassbookPreview(input) {
  const file = input.files[0];
  if (!file) return;
  const label = document.getElementById('passbook-file-label');
  label.textContent = `File: ${file.name} (${(file.size / 1024).toFixed(1)} KB)`;
  if (file.type.startsWith('image/')) {
    const reader = new FileReader();
    reader.onload = e => {
      document.getElementById('passbook-img').src = e.target.result;
      document.getElementById('passbook-preview').style.display = 'block';
    };
    reader.readAsDataURL(file);
  } else {
    document.getElementById('passbook-preview').style.display = 'none';
  }
}

async function submitRegister() {
  const payload = getFormPayload();
  if (!payload) return;
  try {
    const res = await api.registerStudent(payload);
    const id = res.data.id;
    // Check for passbook
    const passbookInput = document.getElementById('f-passbook');
    if (passbookInput.files.length > 0) {
      await api.uploadDocument(id, 'PASSBOOK', '', passbookInput.files[0]);
    }
    toast('Student registered successfully!', 'success');
    navigate('student-detail', id);
  } catch (e) { toast(e.message, 'error'); }
}

async function submitUpdate() {
  const payload = getFormPayload();
  if (!payload) return;
  try {
    await api.updateStudent(currentStudentId, payload);
    const passbookInput = document.getElementById('f-passbook');
    if (passbookInput.files.length > 0) {
      await api.uploadDocument(currentStudentId, 'PASSBOOK', '', passbookInput.files[0]);
    }
    toast('Student updated successfully!', 'success');
    navigate('student-detail', currentStudentId);
  } catch (e) { toast(e.message, 'error'); }
}

function getFormPayload() {
  const samagra = document.getElementById('f-samagraId').value.trim();
  const name = document.getElementById('f-fullName').value.trim();
  const mobile = document.getElementById('f-mobile').value.trim();
  if (!samagra || !name || !mobile) { toast('Please fill Samagra ID, Name, and Mobile', 'error'); return null; }

  const subs = Array.from(document.getElementById('selected-subjects').children).map(c => ({
    subjectName: c.getAttribute('data-name'),
    subjectCode: c.getAttribute('data-code')
  }));

  return {
    personalInfo: {
      samagraId: samagra, fullName: name, gender: document.getElementById('f-gender').value,
      dateOfBirth: document.getElementById('f-dob').value, bloodGroup: document.getElementById('f-blood').value,
      category: document.getElementById('f-category').value, religion: document.getElementById('f-religion').value,
      nationality: document.getElementById('f-nationality').value, fatherName: document.getElementById('f-father').value,
      motherName: document.getElementById('f-mother').value, mobileNumber: mobile, email: document.getElementById('f-email').value,
      state: document.getElementById('f-state').value, city: document.getElementById('f-city').value,
      pincode: document.getElementById('f-pincode').value, address: document.getElementById('f-address').value,
      admissionDate: document.getElementById('f-admDate').value, studentStatus: document.getElementById('f-status').value
    },
    academicDetails: {
      className: document.getElementById('f-class').value, section: document.getElementById('f-section').value,
      rollNumber: document.getElementById('f-roll').value, admissionNumber: document.getElementById('f-admNo').value,
      board: document.getElementById('f-board').value, academicYear: document.getElementById('f-year').value,
      stream: document.getElementById('f-stream').value, previousSchool: document.getElementById('f-prevSchool').value,
      previousPercentage: document.getElementById('f-prevPct').value
    },
    bankDetails: {
      bankName: document.getElementById('f-bankName').value, branchName: document.getElementById('f-branch').value,
      ifscCode: document.getElementById('f-ifsc').value, accountNumber: document.getElementById('f-accNo').value,
      accountHolderName: document.getElementById('f-accHolder').value
    },
    subjects: subs
  };
}

// ============ DOCUMENTS ============
function showUploadDoc(id) {
  document.getElementById('modal-content').innerHTML = `
    <h3 style="margin-bottom:16px">📤 Upload Document</h3>
    <div class="form-group"><label class="form-label">Document Type *</label>
      <select id="m-doc-type" class="form-control">
        <option>AADHAAR</option><option>SAMAGRA</option><option>CASTE_CERTIFICATE</option>
        <option>INCOME_CERTIFICATE</option><option>DOMICILE</option><option>MARKSHEET_10</option>
        <option>MARKSHEET_12</option><option>TRANSFER_CERTIFICATE</option><option>MIGRATION</option>
        <option>PHOTO</option><option>SIGNATURE</option><option>OTHER</option>
      </select>
    </div>
    <div class="form-group"><label class="form-label">Document Number</label><input id="m-doc-no" class="form-control"/></div>
    <div class="form-group"><label class="form-label">File *</label><input id="m-doc-file" type="file" class="form-control" accept=".pdf,.jpg,.jpeg,.png"/></div>
    <div style="display:flex;gap:10px;justify-content:flex-end;margin-top:16px">
      <button class="btn btn-secondary" onclick="closeModal()">Cancel</button>
      <button class="btn btn-primary" onclick="doUploadDoc(${id})">Upload</button>
    </div>`;
  openModal();
}

function showUploadDocType(id, type) {
  showUploadDoc(id);
  document.getElementById('m-doc-type').value = type;
}

async function doUploadDoc(sid) {
  const type = document.getElementById('m-doc-type').value;
  const no = document.getElementById('m-doc-no').value.trim();
  const fileInput = document.getElementById('m-doc-file');
  if (fileInput.files.length === 0) return toast('Please select a file', 'error');
  try {
    await api.uploadDocument(sid, type, no, fileInput.files[0]);
    toast('Document uploaded', 'success');
    closeModal();
    renderStudentDetail(sid);
  } catch (e) { toast(e.message, 'error'); }
}

async function updateDocStatus(did, status, sid) {
  try {
    await api.updateDocumentStatus(did, status);
    toast('Status updated', 'success');
    renderStudentDetail(sid);
  } catch (e) { toast(e.message, 'error'); }
}

async function deleteDoc(did, sid) {
  if (!confirm('Delete this document?')) return;
  try {
    await api.deleteDocument(did);
    toast('Document deleted', 'success');
    renderStudentDetail(sid);
  } catch (e) { toast(e.message, 'error'); }
}

async function viewDocument(did, filename) {
  try {
    const blob = await api.getDocument(did);
    const url = URL.createObjectURL(blob);
    if (filename.toLowerCase().endsWith('.pdf')) {
      window.open(url, '_blank');
    } else {
      document.getElementById('modal-content').innerHTML = `
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px">
          <h3>📎 ${filename}</h3>
          <button class="btn btn-secondary btn-sm" onclick="closeModal()">✕ Close</button>
        </div>
        <div style="text-align:center;background:#000;border-radius:8px;padding:10px">
          <img src="${url}" style="max-width:100%;max-height:70vh;object-fit:contain"/>
        </div>`;
      openModal();
    }
  } catch (e) { toast(e.message, 'error'); }
}

async function downloadDocument(did, filename) {
  try {
    const blob = await api.getDocument(did);
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  } catch (e) { toast(e.message, 'error'); }
}

// ============ UI UTILS ============
function toast(msg, type = 'success') {
  const el = document.getElementById('toast');
  el.textContent = msg;
  el.className = `toast ${type}`;
  el.style.display = 'block';
  setTimeout(() => el.style.display = 'none', 3000);
}

function openModal() { document.getElementById('modal-overlay').classList.remove('hidden'); }
function closeModal() { document.getElementById('modal-overlay').classList.add('hidden'); }
