import api from '@/assets/js/axios'; 

export async function checkAuth() {
  try {
    const res = await api.get('/usuarios/me');
    return !!res.data;
  } catch {
    return false;
  }
}