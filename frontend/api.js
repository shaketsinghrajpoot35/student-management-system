/* api.js — Axios-like fetch wrapper for the SmartStudent backend */
const BASE_URL = '';

const api = {
  getToken: () => localStorage.getItem('token'),

  headers(extra = {}) {
    const h = { 'Content-Type': 'application/json', ...extra };
    const t = this.getToken();
    if (t) h['Authorization'] = `Bearer ${t}`;
    return h;
  },

  async request(method, path, body, isMultipart = false) {
    const opts = { method, headers: {} };
    const t = this.getToken();
    if (t) opts.headers['Authorization'] = `Bearer ${t}`;
    if (isMultipart) {
      opts.body = body; // FormData
    } else if (body) {
      opts.headers['Content-Type'] = 'application/json';
      opts.body = JSON.stringify(body);
    }
    const res = await fetch(BASE_URL + path, opts);
    if (res.status === 401 || res.status === 403) {
      if (path !== '/api/auth/login') { logout(); throw new Error('Session expired'); }
    }
    const data = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(data.message || `Error ${res.status}`);
    return data;
  },

  get: (path) => api.request('GET', path),
  post: (path, body) => api.request('POST', path, body),
  put: (path, body) => api.request('PUT', path, body),
  del: (path) => api.request('DELETE', path),
  multipart: (method, path, formData) => api.request(method, path, formData, true),

  // AUTH
  login: (u, p) => api.post('/api/auth/login', { username: u, password: p }),
  register: (email, password, schoolName) => api.post('/api/auth/register', { email, password, schoolName }),

  // STUDENTS
  getStudents: (params = '') => api.get(`/api/students?${params}`),
  getStudent: (id) => api.get(`/api/students/${id}`),
  getFullDetails: (id) => api.get(`/api/students/${id}/full-details`),
  deleteStudent: (id) => api.del(`/api/students/${id}`),
  registerStudent: (fd) => api.multipart('POST', '/api/students/register', fd),
  updateStudent: (id, fd) => api.multipart('PUT', `/api/students/${id}`, fd),

  // ACADEMIC
  getAcademic: (studentId) => api.get(`/api/academic/${studentId}`),
  updateAcademic: (id, body) => api.put(`/api/academic/${id}`, body),

  // SUBJECTS
  getAllSubjects: () => api.get('/api/subjects'),
  createSubject: (body) => api.post('/api/subjects', body),
  updateSubject: (id, body) => api.put(`/api/subjects/${id}`, body),
  deleteSubject: (id) => api.del(`/api/subjects/${id}`),
  assignSubjects: (studentId, body) => api.post(`/api/students/${studentId}/subjects`, body),

  // DOCUMENTS
  getDocuments: (studentId) => api.get(`/api/documents/student/${studentId}`),
  deleteDocument: (id) => api.del(`/api/documents/${id}`),
  uploadDocument: (studentId, fd) => {
    const t = api.getToken();
    const opts = { method: 'POST', headers: { Authorization: `Bearer ${t}` }, body: fd };
    return fetch(`${BASE_URL}/api/documents/upload?studentId=${studentId}`, opts)
      .then(r => r.json());
  },
  updateDocument: (id, fd) => {
    const t = api.getToken();
    const opts = { method: 'PUT', headers: { Authorization: `Bearer ${t}` }, body: fd };
    return fetch(`${BASE_URL}/api/documents/${id}`, opts)
      .then(async r => {
        const text = await r.text();
        return text ? JSON.parse(text) : {};
      });
  },
  downloadUrl: (id) => `${BASE_URL}/api/documents/download/${id}`,

  // BANK
  getBank: (studentId) => api.get(`/api/bank-details/${studentId}`),
  updateBank: (id, body) => api.put(`/api/bank-details/${id}`, body),
};
