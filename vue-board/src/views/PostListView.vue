<template>
  <div class="bg-white shadow sm:rounded-lg">
    <div class="px-4 py-5 sm:p-6">
      <div class="flex justify-between items-center mb-6">
        <h3 class="text-lg leading-6 font-medium text-gray-900">게시글 목록</h3>
        <router-link
          to="/posts/new"
          class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
        >
          글쓰기
        </router-link>
      </div>

      <div class="space-y-4">
        <div v-for="post in posts" :key="post.id" class="border rounded-lg p-4 hover:bg-gray-50">
          <router-link :to="`/posts/${post.id}`" class="block">
            <h4 class="text-lg font-medium text-gray-900">{{ post.title }}</h4>
            <p class="mt-1 text-sm text-gray-500">
              작성자: {{ post.authorEmail }} | 작성일: {{ formatDate(post.createdAt) }}
            </p>
          </router-link>
          <div class="mt-2 flex justify-end space-x-2">
            <button
              v-if="canEdit(post)"
              @click="editPost(post.id)"
              class="text-sm text-indigo-600 hover:text-indigo-900"
            >
              수정
            </button>
            <button
              v-if="canEdit(post)"
              @click="deletePost(post.id)"
              class="text-sm text-red-600 hover:text-red-900"
            >
              삭제
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import type { Post } from '@/types';
import api from '@/api/axios';
import { useAuthStore } from '@/store/auth';

const router = useRouter();
const authStore = useAuthStore();
const posts = ref<Post[]>([]);

const fetchPosts = async () => {
  try {
    const response = await api.get('/posts');
    posts.value = response.data;
  } catch (error) {
    alert('게시글 목록을 불러오는데 실패했습니다.');
  }
};

const canEdit = (post: Post) => {
  return post.authorEmail === authStore.user?.email || authStore.user?.role === 'ADMIN';
};

const editPost = (id: number) => {
  router.push(`/posts/${id}/edit`);
};

const deletePost = async (id: number) => {
  if (!confirm('정말 삭제하시겠습니까?')) return;

  try {
    await api.delete(`/posts/${id}`);
    await fetchPosts();
  } catch (error) {
    alert('게시글 삭제에 실패했습니다.');
  }
};

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('ko-KR');
};

onMounted(fetchPosts);
</script> 