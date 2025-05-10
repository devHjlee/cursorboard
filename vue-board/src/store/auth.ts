import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { User } from '@/types';
import api from '@/api/axios';
import router from '@/router';

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null);
  const token = ref<string | null>(localStorage.getItem('token'));

  const login = async (email: string, password: string) => {
    try {
      const response = await api.post('/auth/login', { email, password });
      const { data } = response;
      user.value = data;
      token.value = data.token;
      localStorage.setItem('token', data.token);
      router.push('/posts');
    } catch (error) {
      throw error;
    }
  };

  const logout = () => {
    user.value = null;
    token.value = null;
    localStorage.removeItem('token');
    router.push('/login');
  };

  const isAuthenticated = () => {
    return !!token.value;
  };

  return {
    user,
    token,
    login,
    logout,
    isAuthenticated,
  };
}); 