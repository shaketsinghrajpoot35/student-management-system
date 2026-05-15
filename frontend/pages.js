/* pages.js — HTML generators for each page */

const Pages = {

  home: () => `
<div class="home-page fade-in">
  <nav class="home-nav">
    <div class="logo" onclick="navigate('home')">
      <span class="logo-icon">🎓</span>
      <span class="logo-text">SmartStudent</span>
    </div>
    <div class="nav-links">
      <button class="btn btn-secondary" onclick="navigate('login')">Login</button>
      <button class="btn btn-primary" onclick="navigate('signup')">Sign Up</button>
    </div>
  </nav>

  <section class="hero-section">
    <div class="hero-content">
      <h1 class="hero-title">Modern Student Management <span class="text-gradient">Redefined</span></h1>
      <p class="hero-subtitle">Secure, encrypted, and lightning-fast record management for modern educational institutions. Manage students, documents, and academic records with military-grade security.</p>
      <div class="hero-btns">
        <button class="btn btn-primary btn-lg" onclick="navigate('signup')">Get Started for Free</button>
        <button class="btn btn-secondary btn-lg" onclick="navigate('login')">Admin Portal</button>
      </div>
    </div>
    <div class="hero-visual">
      <div class="glass-card visual-card">
        <div class="visual-header">
          <div class="dot red"></div><div class="dot yellow"></div><div class="dot green"></div>
        </div>
        <div class="visual-body">
          <div class="skeleton-line long"></div>
          <div class="skeleton-line medium"></div>
          <div class="skeleton-grid">
            <div class="skeleton-box"></div><div class="skeleton-box"></div><div class="skeleton-box"></div>
          </div>
        </div>
      </div>
    </div>
  </section>

  <section class="features-section">
    <div class="feature-card glass-card">
      <div class="feature-icon">🛡️</div>
      <h3>End-to-End Encryption</h3>
      <p>Your student data is protected with AES-256-GCM encryption at rest and in transit.</p>
    </div>
    <div class="feature-card glass-card">
      <div class="feature-icon">📁</div>
      <h3>Document Vault</h3>
      <p>Securely store Aadhaar, Marksheets, and Certificates directly in the cloud database.</p>
    </div>
    <div class="feature-card glass-card">
      <div class="feature-icon">📊</div>
      <h3>Advanced Search</h3>
      <p>Find records instantly using blind indexing and multi-field filtering.</p>
    </div>
  </section>

  <footer class="home-footer">
    <div class="footer-content">
      <div class="footer-brand">
        <div class="logo">
          <span class="logo-icon">🎓</span>
          <span class="logo-text">SmartStudent</span>
        </div>
        <p>Built for security-first schools by <strong>Shaket Singh Rajpoot</strong>.</p>
      </div>
      <div class="footer-social">
        <h4>Connect with me</h4>
        <div class="social-links">
          <a href="https://github.com/shaketsinghrajpoot35" target="_blank" class="social-icon">GitHub</a>
          <a href="https://linkedin.com/in/shaket-singh-rajpoot" target="_blank" class="social-icon">LinkedIn</a>
          <a href="https://twitter.com/shaket_rajpoot" target="_blank" class="social-icon">Twitter</a>
          <a href="https://www.instagram.com/shaket_.singh_rajpoot24?igsh=MWdlODM5dXJqaXNjYg==" target="_blank" class="social-icon">Instagram</a>
        </div>
      </div>
    </div>
    <div class="footer-bottom">
      &copy; 2026 SmartStudent | Developed by <strong>Shaket Singh Rajpoot</strong>
    </div>
  </footer>
</div>`,

  login: () => `
<div class="login-page">
  <div class="login-card fade-in">
    <div class="login-logo">
      <div class="logo" onclick="navigate('home')" style="justify-content:center;margin-bottom:24px">
        <span class="logo-icon" style="font-size:40px">🎓</span>
        <span class="logo-text" style="font-size:24px">SmartStudent</span>
      </div>
      <h1>Admin Login</h1>
      <p>Secure Access to Portal</p>
    </div>
    <div id="login-error" class="login-error"></div>
    <div class="form-group">
      <label class="form-label">Email or Username</label>
      <input id="username" class="form-control" placeholder="admin@school.edu or admin" type="text" autocomplete="username"/>
    </div>
    <div class="form-group">
      <label class="form-label">Password</label>
      <input id="password" class="form-control" placeholder="••••••••" type="password" autocomplete="current-password"/>
    </div>
    <button class="btn btn-primary btn-full" style="margin-top:8px" onclick="doLogin()">Login to Portal</button>
    <div style="margin-top:16px;text-align:center;font-size:14px;color:var(--text-muted)">
      Don't have an account? <a href="#" onclick="navigate('signup')" style="color:var(--primary);text-decoration:none;font-weight:500">Sign Up</a>
    </div>
  </div>
</div>`,

  signup: () => `
<div class="login-page">
  <div class="login-card fade-in">
    <div class="login-logo">
      <div class="logo" onclick="navigate('home')" style="justify-content:center;margin-bottom:24px">
        <span class="logo-icon" style="font-size:40px">🎓</span>
        <span class="logo-text" style="font-size:24px">SmartStudent</span>
      </div>
      <h1>Sign Up</h1>
      <p>Create School Admin Account</p>
    </div>
    <div id="signup-error" class="login-error"></div>
    <div class="form-group">
      <label class="form-label">Email</label>
      <input id="su-email" class="form-control" placeholder="admin@school.edu" type="email"/>
    </div>
    <div class="form-group">
      <label class="form-label">Password</label>
      <input id="su-password" class="form-control" placeholder="••••••••" type="password"/>
    </div>
    <div class="form-group">
      <label class="form-label">School Name</label>
      <input id="su-school" class="form-control" placeholder="e.g. Springfield High School" type="text"/>
    </div>
    <button class="btn btn-primary btn-full" style="margin-top:8px" onclick="doSignup()">Create Account</button>
    <div style="margin-top:16px;text-align:center;font-size:14px;color:var(--text-muted)">
      Already have an account? <a href="#" onclick="navigate('login')" style="color:var(--primary);text-decoration:none;font-weight:500">Login</a>
    </div>
  </div>
</div>`,


  dashboard: (stats) => `
<div class="fade-in">
  <div class="page-header">
    <div><div class="page-title">Dashboard</div><div class="page-subtitle">Welcome back, Administrator</div></div>
  </div>
  <div class="stats-grid">
    <div class="stat-card"><div class="stat-icon">👥</div><div class="stat-value">${stats.total || 0}</div><div class="stat-label">Total Students</div></div>
    <div class="stat-card"><div class="stat-icon">✅</div><div class="stat-value">${stats.active || 0}</div><div class="stat-label">Active Students</div></div>
    <div class="stat-card"><div class="stat-icon">📚</div><div class="stat-value">${stats.subjects || 0}</div><div class="stat-label">Total Subjects</div></div>
    <div class="stat-card"><div class="stat-icon">📁</div><div class="stat-value">${stats.docs || 0}</div><div class="stat-label">Documents Uploaded</div></div>
  </div>
  <div style="display:grid;grid-template-columns:1fr 1fr;gap:16px;flex-wrap:wrap">
    <div class="card">
      <div class="card-header"><span class="card-title">Quick Actions</span></div>
      <div style="display:flex;flex-direction:column;gap:10px">
        <button class="btn btn-primary" onclick="navigate('register')">➕ Register New Student</button>
        <button class="btn btn-secondary" onclick="navigate('students')">👥 View All Students</button>
        <button class="btn btn-secondary" onclick="navigate('subjects')">📚 Manage Subjects</button>
      </div>
    </div>
    <div class="card">
      <div class="card-header"><span class="card-title">Recent Students</span></div>
      <div id="recent-list"><div class="loading"><div class="spinner"></div></div></div>
    </div>
  </div>
</div>`,

  students: (data, search) => `
<div class="fade-in">
  <div class="page-header">
    <div><div class="page-title">Students</div><div class="page-subtitle">${data.totalElements || 0} students found</div></div>
    <div>
      <button class="btn btn-secondary" onclick="exportStudentsCsv()">📥 Export CSV</button>
      <button class="btn btn-primary" onclick="navigate('register')">➕ Register Student</button>
    </div>
  </div>
  <div class="card">
    <div class="search-row">
      <input id="s-name" class="form-control search-input" placeholder="Search by name..." value="${search.name || ''}" onkeyup="if(event.key==='Enter') searchStudents()"/>
      <input id="s-samagra" class="form-control" style="width:140px" placeholder="Samagra ID" value="${search.samagraId || ''}" onkeyup="if(event.key==='Enter') searchStudents()"/>
      <input id="s-admNo" class="form-control" style="width:140px" placeholder="Admission No" value="${search.admNo || ''}" onkeyup="if(event.key==='Enter') searchStudents()"/>
      <select id="s-class" class="form-control" style="width:130px" onchange="searchStudents()">
        <option value="">All Classes</option>
        ${['Class 1', 'Class 2', 'Class 3', 'Class 4', 'Class 5', 'Class 6', 'Class 7', 'Class 8', 'Class 9', 'Class 10', 'Class 11', 'Class 12', 'Undergraduate', 'Postgraduate', 'Diploma', 'PhD', 'Other'].map(v => `<option ${search.className === v ? 'selected' : ''} value="${v}">${v}</option>`).join('')}
      </select>
      <select id="s-stream" class="form-control" style="width:130px" onchange="searchStudents()">
        <option value="">All Streams</option>
        ${['PCM', 'PCB', 'PCMB', 'COMMERCE', 'ARTS', 'GENERAL'].map(s => `<option ${search.stream === s ? 'selected' : ''}>${s}</option>`).join('')}
      </select>
      <button class="btn btn-primary" onclick="searchStudents()">🔍 Search</button>
      <button class="btn btn-secondary" onclick="clearSearch()">✕ Clear</button>
    </div>
    <div class="table-wrap">
      <table>
        <thead><tr><th>#</th><th>Name</th><th>Adm No</th><th>Samagra ID</th><th>Mobile</th><th>Status</th><th>Actions</th></tr></thead>
        <tbody>
          ${(data.content || []).length === 0 ? `<tr><td colspan="7"><div class="empty-state"><div class="empty-icon">🔍</div><p>No students found</p></div></td></tr>` :
      (data.content || []).map((s, i) => `
          <tr>
            <td>${(data.pageNumber * data.pageSize) + i + 1}</td>
            <td><strong>${s.fullName}</strong></td>
            <td><span class="badge badge-blue">${s.admissionNumber || '-'}</span></td>
            <td><span class="badge badge-gray">${s.samagraId}</span></td>
            <td>${s.mobileNumber || '-'}</td>
            <td><span class="badge ${s.studentStatus === 'ACTIVE' ? 'badge-green' : 'badge-yellow'}">${s.studentStatus}</span></td>
            <td><div class="td-actions">
              <button class="btn btn-info btn-sm" onclick="downloadRegistrationForm(${s.id})">📄 PDF</button>
              <button class="btn btn-info btn-sm" onclick="navigate('student-detail',${s.id})">👁 View</button>
              <button class="btn btn-secondary btn-sm" onclick="navigate('edit',${s.id})">✏️ Edit</button>
              <button class="btn btn-danger btn-sm" onclick="confirmDelete(${s.id},'${s.fullName}')">🗑</button>
            </div></td>
          </tr>`).join('')}
        </tbody>
      </table>
    </div>
    <div class="pagination" id="pagination"></div>
  </div>
</div>`,

  subjects: (list) => `
<div class="fade-in">
  <div class="page-header">
    <div><div class="page-title">Subjects</div><div class="page-subtitle">${list.length} subjects</div></div>
    <button class="btn btn-primary" onclick="showAddSubject()">➕ Add Subject</button>
  </div>
  <div class="card">
    <div class="chips">
      ${list.length === 0 ? '<p style="color:var(--text-muted)">No subjects yet.</p>' :
      list.map(s => `<div class="chip">📚 ${s.subjectName} <small>[${s.subjectCode || '-'}]</small></div>`).join('')}
    </div>
    <div style="margin-top:24px">
    <table><thead><tr><th>ID</th><th>Subject Name</th><th>Code</th><th>Description</th><th>Actions</th></tr></thead>
    <tbody>${list.map(s => `<tr><td>${s.id}</td><td><strong>${s.subjectName}</strong></td><td><span class="badge badge-blue">${s.subjectCode || '-'}</span></td><td>${s.description || '-'}</td>
      <td>
        <div class="td-actions">
          <button class="btn btn-secondary btn-sm" onclick="showEditSubject(${s.id}, '${s.subjectName}', '${s.subjectCode || ''}', '${s.description || ''}')">✏️ Edit</button>
          <button class="btn btn-danger btn-sm" onclick="confirmDeleteSubject(${s.id}, '${s.subjectName}')">🗑 Delete</button>
        </div>
      </td>
    </tr>`).join('')}</tbody>
    </table></div>
  </div>
</div>`,

  studentDetail: (fd) => {
    const s = fd.personalInfo || {};
    const ac = fd.academicDetails || {};
    const bk = fd.bankDetails || {};
    const docs = fd.documents || [];
    const subs = fd.subjects || [];
    return `
<div class="fade-in">
  <div class="page-header">
    <div>
      <div class="page-title">👤 ${s.fullName}</div>
      <div class="page-subtitle">Samagra ID: ${s.samagraId} &nbsp;|&nbsp; <span class="badge ${s.studentStatus === 'ACTIVE' ? 'badge-green' : 'badge-yellow'}">${s.studentStatus}</span></div>
    </div>
    <div style="display:flex;gap:10px">
      <button class="btn btn-info" onclick="downloadRegistrationForm(${fd.id})">📄 Download Form</button>
      <button class="btn btn-secondary" onclick="navigate('students')">← Back</button>
      <button class="btn btn-primary" onclick="navigate('edit',${fd.id})">✏️ Edit Student</button>
    </div>
  </div>
  <div class="tabs" id="detail-tabs">
    <button class="tab-btn active" onclick="showTab('personal')">👤 Personal</button>
    <button class="tab-btn" onclick="showTab('academic')">🎓 Academic</button>
    <button class="tab-btn" onclick="showTab('subjects')">📚 Subjects</button>
    <button class="tab-btn" onclick="showTab('documents')">📁 Documents</button>
    <button class="tab-btn" onclick="showTab('bank')">🏦 Bank</button>
  </div>
  <div id="tab-personal" class="tab-pane card">
    <div class="form-section-title">Personal Information</div>
    <div class="detail-grid">
      ${[['Full Name', s.fullName], ['Gender', s.gender], ['DOB', s.dateOfBirth], ['Blood Group', s.bloodGroup], ['Category', s.category], ['Religion', s.religion], ['Nationality', s.nationality], ['Father', s.fatherName], ['Mother', s.motherName], ['Guardian', s.guardianName], ['Mobile', s.mobileNumber], ['Alt Mobile', s.alternateMobileNumber], ['Email', s.email], ['Address', s.address], ['City', s.city], ['State', s.state], ['Pincode', s.pincode], ['Admission Date', s.admissionDate], ['Status', s.studentStatus]].map(([l, v]) => `<div class="detail-item"><div class="detail-label">${l}</div><div class="detail-value">${v || '—'}</div></div>`).join('')}
    </div>
  </div>
  <div id="tab-academic" class="tab-pane card" style="display:none">
    <div class="form-section-title">Academic Information</div>
    <div class="detail-grid">
      ${[['Class', ac.className], ['Section', ac.section], ['Roll No', ac.rollNumber], ['Admission No', ac.admissionNumber], ['Board', ac.board], ['Academic Year', ac.academicYear], ['Stream', ac.stream], ['Prev School', ac.previousSchool], ['Prev %', ac.previousPercentage]].map(([l, v]) => `<div class="detail-item"><div class="detail-label">${l}</div><div class="detail-value">${v || '—'}</div></div>`).join('')}
    </div>
  </div>
  <div id="tab-subjects" class="tab-pane card" style="display:none">
    <div class="form-section-title">Subjects (${subs.length})</div>
    <div class="chips">${subs.length === 0 ? '<p style="color:var(--text-muted)">No subjects assigned.</p>' : subs.map(s => `<div class="chip">📚 ${s.subjectName}</div>`).join('')}</div>
  </div>
  <div id="tab-documents" class="tab-pane card" style="display:none">
    <div class="card-header"><span class="form-section-title" style="margin:0">Documents (${docs.length})</span>
      <button class="btn btn-primary btn-sm" onclick="showUploadDoc(${fd.id})">📤 Upload Document</button>
    </div>
    <div class="doc-grid">
      ${docs.length === 0 ? '<div class="empty-state"><div class="empty-icon">📁</div><p>No documents uploaded yet.</p></div>' :
        docs.map(d => `<div class="doc-card">
        <div class="doc-type">${d.documentType}</div>
        <div class="doc-number">No: ${d.documentNumber || '—'}</div>
        ${d.fileName ? `<div style="font-size:11px;color:var(--text-muted);margin-bottom:4px">📎 ${d.fileName}</div>` : ''}
        <div style="font-size:12px;color:var(--text-muted);margin-bottom:10px">${d.uploadDate ? d.uploadDate.substring(0, 10) : ''} &nbsp;
          <select class="form-control" style="display:inline-block;width:auto;padding:2px 8px;font-size:11px;height:auto" onchange="updateDocStatus(${d.id}, this.value, ${fd.id})">
            <option value="PENDING" ${d.verificationStatus === 'PENDING' ? 'selected' : ''}>Pending</option>
            <option value="VERIFIED" ${d.verificationStatus === 'VERIFIED' ? 'selected' : ''}>Verified</option>
            <option value="REUPLOAD_REQUESTED" ${d.verificationStatus === 'REUPLOAD_REQUESTED' ? 'selected' : ''}>Upload Again</option>
            <option value="REJECTED" ${d.verificationStatus === 'REJECTED' ? 'selected' : ''}>Wrong Document</option>
          </select>
        </div>
        <div class="doc-actions">
          <button class="btn btn-secondary btn-sm" onclick="showEditDoc(${d.id}, ${s.id}, '${d.documentType}', '${d.documentNumber || ''}')">✏️ Edit</button>
          ${d.fileName ? `<button class="btn btn-primary btn-sm" onclick="viewDocument(${d.id},'${d.fileName || 'document'}')">👁 View</button>` : ''}
          ${d.fileName ? `<button class="btn btn-info btn-sm" onclick="downloadDocument(${d.id},'${d.fileName || 'document'}')">⬇ Download</button>` : ''}
          <button class="btn btn-danger btn-sm" onclick="deleteDoc(${d.id},${s.id})">🗑 Delete</button>
        </div>
      </div>`).join('')}
    </div>
  </div>
  <div id="tab-bank" class="tab-pane card" style="display:none">
    <div class="form-section-title">Bank Details</div>
    <div class="detail-grid">
      ${[['Bank', bk.bankName], ['Branch', bk.branchName], ['IFSC', bk.ifscCode], ['Account No', bk.accountNumber], ['Holder', bk.accountHolderName]].map(([l, v]) => `<div class="detail-item"><div class="detail-label">${l}</div><div class="detail-value">${v || '—'}</div></div>`).join('')}
    </div>
    <hr style="border-color:var(--border);margin:20px 0"/>
    <div class="form-section-title" style="margin-bottom:12px">💳 Bank Passbook</div>
    ${(() => {
        const pb = docs.find(d => d.documentType === 'PASSBOOK');
        if (pb) return `
      <div class="doc-card" style="max-width:340px">
        <div class="doc-type">PASSBOOK</div>
        ${pb.fileName ? `<div style="font-size:11px;color:var(--text-muted);margin-bottom:4px">📎 ${pb.fileName}</div>` : ''}
        <div style="font-size:12px;color:var(--text-muted);margin-bottom:10px">
          ${pb.uploadDate ? pb.uploadDate.substring(0, 10) : ''}
          &nbsp;
          <select class="form-control" style="display:inline-block;width:auto;padding:2px 8px;font-size:11px;height:auto" onchange="updateDocStatus(${pb.id}, this.value, ${s.id})">
            <option value="PENDING" ${pb.verificationStatus === 'PENDING' ? 'selected' : ''}>Pending</option>
            <option value="VERIFIED" ${pb.verificationStatus === 'VERIFIED' ? 'selected' : ''}>Verified</option>
            <option value="REUPLOAD_REQUESTED" ${pb.verificationStatus === 'REUPLOAD_REQUESTED' ? 'selected' : ''}>Upload Again</option>
            <option value="REJECTED" ${pb.verificationStatus === 'REJECTED' ? 'selected' : ''}>Wrong Document</option>
          </select>
        </div>
        <div class="doc-actions">
          <button class="btn btn-secondary btn-sm" onclick="showEditDoc(${pb.id}, ${s.id}, '${pb.documentType}', '${pb.documentNumber || ''}')">✏️ Edit</button>
          ${pb.fileName ? `<button class="btn btn-primary btn-sm" onclick="viewDocument(${pb.id},'${pb.fileName || 'passbook'}')">👁 View</button>` : ''}
          ${pb.fileName ? `<button class="btn btn-info btn-sm" onclick="downloadDocument(${pb.id},'${pb.fileName || 'passbook'}')">⬇ Download</button>` : ''}
          <button class="btn btn-danger btn-sm" onclick="deleteDoc(${pb.id},${s.id})">🗑 Delete</button>
        </div>
      </div>`;
        return `
      <div style="display:flex;align-items:center;gap:14px;flex-wrap:wrap">
        <p style="color:var(--text-muted);font-size:13px">No passbook uploaded yet.</p>
        <button class="btn btn-primary btn-sm" onclick="showUploadDocType(${fd.id},'PASSBOOK')">📤 Upload Passbook</button>
      </div>`;
      })()}
  </div>
</div>`;
  },


  registerForm: (prefill, subjectList) => {
    const p = (prefill && prefill.personalInfo) || {};
    const ac = (prefill && prefill.academicDetails) || {};
    const bk = (prefill && prefill.bankDetails) || {};
    const subs = (prefill && prefill.subjects) || [];
    const isEdit = !!prefill;
    return `
<div class="fade-in">
  <div class="page-header">
    <div><div class="page-title">${isEdit ? '✏️ Edit Student' : '➕ Register Student'}</div></div>
    <button class="btn btn-secondary" onclick="navigate(${isEdit ? `'student-detail',currentStudentId` : "'students'"})">← Back</button>
  </div>
  <div class="tabs">
    <button class="tab-btn active" onclick="showFormTab('f-personal')">👤 Personal</button>
    <button class="tab-btn" onclick="showFormTab('f-academic')">🎓 Academic</button>
    <button class="tab-btn" onclick="showFormTab('f-subjects')">📚 Subjects</button>
    <button class="tab-btn" onclick="showFormTab('f-bank')">🏦 Bank</button>
    <button class="tab-btn" onclick="showFormTab('f-documents')">📁 Documents</button>
  </div>

  <!-- PERSONAL -->
  <div id="f-personal" class="tab-pane card">
    <div class="form-section-title">Personal Information</div>
    <div class="form-grid">
      <div class="form-group"><label class="form-label">Samagra ID *</label><input id="f-samagraId" class="form-control" value="${p.samagraId || ''}" placeholder="e.g. SM12345678"/></div>
      <div class="form-group"><label class="form-label">Full Name *</label><input id="f-fullName" class="form-control" value="${p.fullName || ''}" placeholder="Full Name"/></div>
      <div class="form-group"><label class="form-label">Gender *</label>
        <select id="f-gender" class="form-control">${['', 'MALE', 'FEMALE', 'OTHER'].map(v => `<option ${p.gender === v ? 'selected' : ''} value="${v}">${v || 'Select'}</option>`).join('')}</select></div>
      <div class="form-group"><label class="form-label">Date of Birth *</label><input id="f-dob" class="form-control" type="date" value="${p.dateOfBirth || ''}"/></div>
      <div class="form-group"><label class="form-label">Blood Group</label>
        <select id="f-blood" class="form-control">${['', 'A_POSITIVE', 'A_NEGATIVE', 'B_POSITIVE', 'B_NEGATIVE', 'O_POSITIVE', 'O_NEGATIVE', 'AB_POSITIVE', 'AB_NEGATIVE'].map(v => `<option ${p.bloodGroup === v ? 'selected' : ''} value="${v}">${v || 'Select'}</option>`).join('')}</select></div>
      <div class="form-group"><label class="form-label">Category</label>
        <select id="f-category" class="form-control">${['', 'GENERAL', 'OBC', 'SC', 'ST', 'EWS'].map(v => `<option ${p.category === v ? 'selected' : ''} value="${v}">${v || 'Select'}</option>`).join('')}</select></div>
      <div class="form-group"><label class="form-label">Religion</label>
        <select id="f-religion" class="form-control">${['', 'Hindu', 'Muslim', 'Christian', 'Sikh', 'Buddhist', 'Jain', 'Other'].map(v => `<option ${p.religion === v ? 'selected' : ''} value="${v}">${v || 'Select'}</option>`).join('')}</select></div>
      <div class="form-group"><label class="form-label">Nationality</label>
        <select id="f-nationality" class="form-control">${['Indian', 'NRI', 'Other'].map(v => `<option ${(p.nationality || 'Indian') === v ? 'selected' : ''} value="${v}">${v}</option>`).join('')}</select></div>
      <div class="form-group"><label class="form-label">Father's Name</label><input id="f-father" class="form-control" value="${p.fatherName || ''}"/></div>
      <div class="form-group"><label class="form-label">Mother's Name</label><input id="f-mother" class="form-control" value="${p.motherName || ''}"/></div>
      <div class="form-group"><label class="form-label">Mobile *</label><input id="f-mobile" class="form-control" value="${p.mobileNumber || ''}" placeholder="10-digit mobile"/></div>
      <div class="form-group"><label class="form-label">Email</label><input id="f-email" class="form-control" type="email" value="${p.email || ''}"/></div>
      <div class="form-group"><label class="form-label">State</label>
        <select id="f-state" class="form-control">${['', 'Andhra Pradesh', 'Arunachal Pradesh', 'Assam', 'Bihar', 'Chhattisgarh', 'Goa', 'Gujarat', 'Haryana', 'Himachal Pradesh', 'Jharkhand', 'Karnataka', 'Kerala', 'Madhya Pradesh', 'Maharashtra', 'Manipur', 'Meghalaya', 'Mizoram', 'Nagaland', 'Odisha', 'Punjab', 'Rajasthan', 'Sikkim', 'Tamil Nadu', 'Telangana', 'Tripura', 'Uttar Pradesh', 'Uttarakhand', 'West Bengal', 'Andaman and Nicobar Islands', 'Chandigarh', 'Dadra and Nagar Haveli and Daman and Diu', 'Delhi', 'Lakshadweep', 'Puducherry', 'Ladakh', 'Jammu and Kashmir', 'Other'].map(v => `<option ${p.state === v ? 'selected' : ''} value="${v}">${v || 'Select'}</option>`).join('')}</select></div>
      <div class="form-group"><label class="form-label">City</label><input id="f-city" class="form-control" value="${p.city || ''}"/></div>
      <div class="form-group"><label class="form-label">Pincode</label><input id="f-pincode" class="form-control" value="${p.pincode || ''}"/></div>
      <div class="form-group"><label class="form-label">Address</label><input id="f-address" class="form-control" value="${p.address || ''}"/></div>
      <div class="form-group"><label class="form-label">Admission Date</label><input id="f-admDate" class="form-control" type="date" value="${p.admissionDate || ''}"/></div>
      <div class="form-group"><label class="form-label">Status</label>
        <select id="f-status" class="form-control">${['ACTIVE', 'INACTIVE', 'TRANSFERRED', 'DROPPED'].map(v => `<option ${(p.studentStatus || 'ACTIVE') === v ? 'selected' : ''} value="${v}">${v}</option>`).join('')}</select></div>
    </div>
  </div>

  <!-- ACADEMIC -->
  <div id="f-academic" class="tab-pane card" style="display:none">
    <div class="form-section-title">Academic Information</div>
    <div class="form-grid">
      <div class="form-group"><label class="form-label">Class</label>
        <select id="f-class" class="form-control">${['', 'Class 1', 'Class 2', 'Class 3', 'Class 4', 'Class 5', 'Class 6', 'Class 7', 'Class 8', 'Class 9', 'Class 10', 'Class 11', 'Class 12', 'Undergraduate', 'Postgraduate', 'Diploma', 'PhD', 'Other'].map(v => `<option ${ac.className === v ? 'selected' : ''} value="${v}">${v || 'Select'}</option>`).join('')}</select></div>
      <div class="form-group"><label class="form-label">Section</label><input id="f-section" class="form-control" value="${ac.section || ''}"/></div>
      <div class="form-group"><label class="form-label">Roll Number</label><input id="f-roll" class="form-control" value="${ac.rollNumber || ''}"/></div>
      <div class="form-group"><label class="form-label">Admission Number</label><input id="f-admNo" class="form-control" value="${ac.admissionNumber || ''}"/></div>
      <div class="form-group"><label class="form-label">Board</label><input id="f-board" class="form-control" value="${ac.board || ''}"/></div>
      <div class="form-group"><label class="form-label">Academic Year</label><input id="f-year" class="form-control" value="${ac.academicYear || ''}"/></div>
      <div class="form-group"><label class="form-label">Stream</label>
        <select id="f-stream" class="form-control">${['', 'PCM', 'PCB', 'PCMB', 'COMMERCE', 'ARTS', 'GENERAL'].map(v => `<option ${ac.stream === v ? 'selected' : ''} value="${v}">${v || 'Select'}</option>`).join('')}</select></div>
      <div class="form-group"><label class="form-label">Previous School</label><input id="f-prevSchool" class="form-control" value="${ac.previousSchool || ''}"/></div>
      <div class="form-group"><label class="form-label">Previous %</label><input id="f-prevPct" class="form-control" type="number" min="0" max="100" value="${ac.previousPercentage || ''}"/></div>
    </div>
  </div>

  <!-- SUBJECTS -->
  <div id="f-subjects" class="tab-pane card" style="display:none">
    <div class="form-section-title">Subjects</div>
    <div id="selected-subjects" class="chips" style="margin-bottom:16px">
      ${subs.map(s => `<div class="chip" data-code="${s.subjectCode}" data-name="${s.subjectName}">📚 ${s.subjectName} <span class="chip-remove" onclick="removeSubject(this)">✕</span></div>`).join('')}
    </div>
    <select id="subject-picker" class="form-control" style="max-width:300px;display:inline-block;margin-right:8px">
      <option value="">Select a subject to add</option>
      ${subjectList.map(s => `<option value="${s.subjectCode}" data-name="${s.subjectName}">${s.subjectName} [${s.subjectCode || ''}]</option>`).join('')}
    </select>
    <button class="btn btn-secondary" onclick="addSubjectFromPicker()">+ Add</button>
    <hr style="border-color:var(--border);margin:16px 0"/>
    <div class="form-group"><label class="form-label">Or add new subject</label>
      <div style="display:flex;gap:8px">
        <input id="new-sub-name" class="form-control" placeholder="Subject Name"/>
        <input id="new-sub-code" class="form-control" placeholder="Code" style="max-width:100px"/>
        <button class="btn btn-secondary" onclick="addNewSubject()">+ Add</button>
      </div>
    </div>
  </div>

  <!-- BANK -->
  <div id="f-bank" class="tab-pane card" style="display:none">
    <div class="form-section-title">Bank Details</div>
    <div class="form-grid">
      <div class="form-group"><label class="form-label">Bank Name</label><input id="f-bankName" class="form-control" value="${bk.bankName || ''}"/></div>
      <div class="form-group"><label class="form-label">Branch</label><input id="f-branch" class="form-control" value="${bk.branchName || ''}"/></div>
      <div class="form-group"><label class="form-label">IFSC Code</label><input id="f-ifsc" class="form-control" value="${bk.ifscCode || ''}" placeholder="SBIN0001234"/></div>
      <div class="form-group"><label class="form-label">Account Number</label><input id="f-accNo" class="form-control" value="${bk.accountNumber || ''}"/></div>
      <div class="form-group"><label class="form-label">Account Holder</label><input id="f-accHolder" class="form-control" value="${bk.accountHolderName || ''}"/></div>
    </div>
    <hr style="border-color:var(--border);margin:20px 0"/>
    <div class="form-section-title" style="margin-bottom:12px">&#x1F4B3; Bank Passbook Upload</div>
    <div style="display:flex;align-items:flex-start;gap:16px;flex-wrap:wrap">
      <div style="flex:1;min-width:220px">
        <label class="form-label">Passbook File <span style="color:var(--text-muted)">(PDF / JPG / PNG)</span></label>
        <input id="f-passbook" class="form-control" type="file" accept=".pdf,.jpg,.jpeg,.png"
          onchange="showPassbookPreview(this)"/>
      </div>
      <div id="passbook-preview" style="display:none;flex:1;min-width:180px;text-align:center">
        <img id="passbook-img" style="max-height:160px;border-radius:8px;border:1px solid var(--border);object-fit:contain" alt="Preview"/>
        <div id="passbook-file-label" style="font-size:12px;color:var(--text-muted);margin-top:6px"></div>
      </div>
    </div>
    ${isEdit ? `<p style="font-size:12px;color:var(--text-muted);margin-top:10px">&#x26A0; Uploading a new passbook will add it alongside any existing one. Delete the old one from the Bank tab in student details.</p>` : '<p style="font-size:12px;color:var(--text-muted);margin-top:10px">The passbook will be uploaded automatically after student registration.</p>'}
  </div>

  <!-- DOCUMENTS -->
  <div id="f-documents" class="tab-pane card" style="display:none">
    <div class="form-section-title">Documents (uploaded after registration)</div>
    <p style="color:var(--text-muted);font-size:13px;margin-bottom:16px">Register the student first, then upload documents from the student detail page.</p>
    ${isEdit ? `<button class="btn btn-info" onclick="navigate('student-detail',currentStudentId)">📁 Manage Documents</button>` : ''}
  </div>

  <div style="display:flex;gap:12px;margin-top:24px;justify-content:flex-end">
    <button class="btn btn-secondary" onclick="navigate(${isEdit ? `'student-detail',currentStudentId` : "'students'"})">Cancel</button>
    <button class="btn btn-primary" onclick="${isEdit ? 'submitUpdate()' : 'submitRegister()'}">
      ${isEdit ? '💾 Save Changes' : '✅ Register Student'}
    </button>
  </div>
</div>`;
  }
};
