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
    navigate('login');
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
    case 'login': renderLogin(); break;
    case 'dashboard': renderDashboard(); break;
    case 'students': pageNum = 0; renderStudents(); break;
    case 'register': renderRegisterForm(null); break;
    case 'edit': renderEditForm(id); break;
    case 'student-detail': renderStudentDetail(id); break;
    case 'subjects': renderSubjects(); break;
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
    showSidebar();
    document.getElementById('admin-name').textContent = res.data.fullName || res.data.username;
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
  navigate('login');
}

function showSidebar() {
  document.getElementById('sidebar').classList.remove('hidden');
  document.getElementById('main-content').classList.remove('full-width');
  const name = localStorage.getItem('adminName');
  if (name) document.getElementById('admin-name').textContent = name;
}

function hideSidebar() {
  document.getElementById('sidebar').classList.add('hidden');
  document.getElementById('main-content').classList.add('full-width');
}

// ============ LOGIN ============
function renderLogin() {
  hideSidebar();
  document.getElementById('page-container').innerHTML = Pages.login();
}

// ============ DASHBOARD ============
async function renderDashboard() {
  try {
    const [studRes, subRes] = await Promise.all([api.getStudents('size=5&sortBy=createdAt&sortDir=desc'), api.getAllSubjects()]);
    const all = await api.getStudents('size=1000');
    const active = (all.data?.content || []).filter(s => s.studentStatus === 'ACTIVE').length;
    const stats = { total: studRes.data?.totalElements || 0, active, subjects: subRes.data?.length || 0, docs: 0 };
    document.getElementById('page-container').innerHTML = Pages.dashboard(stats);
    const recent = studRes.data?.content || [];
    document.getElementById('recent-list').innerHTML = recent.length === 0
      ? '<p style="color:var(--text-muted);font-size:13px">No students yet.</p>'
      : recent.map(s => `<div style="display:flex;justify-content:space-between;align-items:center;padding:8px 0;border-bottom:1px solid var(--border)">
          <div><strong style="font-size:13px">${s.fullName}</strong><br><span style="font-size:11px;color:var(--text-muted)">${s.samagraId}</span></div>
          <button class="btn btn-secondary btn-sm" onclick="navigate('student-detail',${s.id})">View</button>
        </div>`).join('');
  } catch (e) { toast(e.message, 'error'); }
}

// ============ STUDENTS LIST ============
async function renderStudents() {
  try {
    const params = new URLSearchParams({
      page: pageNum, size: 10, sortBy: 'fullName', sortDir: 'asc',
      ...(searchState.name && { name: searchState.name }),
      ...(searchState.samagraId && { samagraId: searchState.samagraId }),
      ...(searchState.className && { className: searchState.className }),
      ...(searchState.stream && { stream: searchState.stream }),
    });
    const res = await api.getStudents(params.toString());
    const data = res.data || {};
    document.getElementById('page-container').innerHTML = Pages.students(data, searchState);
    renderPagination(data.totalPages || 0);
  } catch (e) { toast(e.message, 'error'); }
}

function searchStudents() {
  searchState = {
    name: document.getElementById('s-name')?.value || '',
    samagraId: document.getElementById('s-samagra')?.value || '',
    className: document.getElementById('s-class')?.value || '',
    stream: document.getElementById('s-stream')?.value || '',
  };
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
  let html = `<button class="page-btn" onclick="goPage(${pageNum-1})" ${pageNum===0?'disabled':''}>‹ Prev</button>`;
  for (let i = 0; i < total; i++) {
    if (total > 7 && Math.abs(i - pageNum) > 2 && i !== 0 && i !== total - 1) { if (Math.abs(i - pageNum) === 3) html += '<span style="color:var(--text-muted)">…</span>'; continue; }
    html += `<button class="page-btn ${i===pageNum?'active':''}" onclick="goPage(${i})">${i+1}</button>`;
  }
  html += `<button class="page-btn" onclick="goPage(${pageNum+1})" ${pageNum>=total-1?'disabled':''}>Next ›</button>`;
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
  event.target.classList.add('active');
}

async function deleteDoc(docId, studentId) {
  if (!confirm('Delete this document?')) return;
  try {
    await api.deleteDocument(docId);
    toast('Document deleted', 'success');
    renderStudentDetail(studentId);
  } catch (e) { toast(e.message, 'error'); }
}

function showUploadDoc(studentId) {
  document.getElementById('modal-content').innerHTML = `
    <h3 style="margin-bottom:16px">📤 Upload Document</h3>
    <div class="form-group"><label class="form-label">Document Type *</label>
      <select id="u-docType" class="form-control">
        ${['AADHAAR','SAMAGRA_ID','APAAR_ID','PAN_CARD','INCOME_CERTIFICATE','DOMICILE_CERTIFICATE','BIRTH_CERTIFICATE','CASTE_CERTIFICATE','TRANSFER_CERTIFICATE','ADMISSION_FORM','MP_TASS','MARKSHEET','STUDENT_PHOTO','PASSBOOK'].map(t=>`<option>${t}</option>`).join('')}
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
    personalInfo: { samagraId: g('f-samagraId'), fullName: g('f-fullName'), gender: g('f-gender'), dateOfBirth: g('f-dob'), bloodGroup: g('f-blood')||undefined, category: g('f-category')||undefined, religion: g('f-religion'), nationality: g('f-nationality'), fatherName: g('f-father'), motherName: g('f-mother'), mobileNumber: g('f-mobile'), email: g('f-email'), address: g('f-address'), city: g('f-city'), state: g('f-state'), pincode: g('f-pincode'), admissionDate: g('f-admDate')||undefined, studentStatus: g('f-status') },
    academicInfo: { className: g('f-class'), section: g('f-section'), rollNumber: g('f-roll'), admissionNumber: g('f-admNo'), board: g('f-board'), academicYear: g('f-year'), stream: g('f-stream')||undefined, previousSchool: g('f-prevSchool'), previousPercentage: g('f-prevPct')||undefined },
    bankDetails: { bankName: g('f-bankName'), branchName: g('f-branch'), ifscCode: g('f-ifsc')||undefined, accountNumber: g('f-accNo'), accountHolderName: g('f-accHolder') },
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
    toast('Student registered successfully!', 'success');
    navigate('student-detail', res.data.id);
  } catch (e) { toast(e.message, 'error'); }
}

async function submitUpdate() {
  const payload = getFormData();
  try {
    await api.updateStudent(currentStudentId, buildFormData(payload));
    toast('Student updated successfully!', 'success');
    navigate('student-detail', currentStudentId);
  } catch (e) { toast(e.message, 'error'); }
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
function closeModal() { document.getElementById('modal-overlay').classList.add('hidden'); }

// ============ TOAST ============
function toast(msg, type = 'info') {
  const el = document.getElementById('toast');
  el.textContent = msg; el.className = `toast ${type}`;
  el.classList.remove('hidden');
  clearTimeout(el._timer);
  el._timer = setTimeout(() => el.classList.add('hidden'), 3500);
}
