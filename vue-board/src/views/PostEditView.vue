<template>
  <div class="bg-white shadow sm:rounded-lg">
    <div class="px-4 py-5 sm:p-6">
      <h3 class="text-lg leading-6 font-medium text-gray-900 mb-6">게시글 수정</h3>

      <form @submit.prevent="handleSubmit" class="space-y-6">
        <div>
          <label for="title" class="block text-sm font-medium text-gray-700">제목</label>
          <input
            type="text"
            id="title"
            v-model="title"
            required
            class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
          />
        </div>

        <div>
          <label for="content" class="block text-sm font-medium text-gray-700">내용</label>
          <textarea
            id="content"
            v-model="content"
            rows="10"
            required
            class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
          ></textarea>
        </div>

        <div class="flex justify-end space-x-3">
          <button
            type="button"
            @click="router.push(`/posts/${route.params.id}`)"
            class="inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            취소
          </button>
          <button
            type="submit"
            class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            수정
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import type { Post } from '@/types';
import api from '@/api/axios';

const route = useRoute();
const router = useRouter();
const title = ref('');
const content = ref('');

const fetchPost = async () => {
  try {
    const response = await api.get(`/posts/${route.params.id}`);
    const post: Post = response.data;
    title.value = post.title;
    content.value = post.content;
  } catch (error) {
    alert('게시글을 불러오는데 실패했습니다.');
    router.push('/posts');
  }
};

const handleSubmit = async () => {
  try {
    await api.put(`/posts/${route.params.id}`, {
      title: title.value,
      content: content.value,
    });
    router.push(`/posts/${route.params.id}`);
  } catch (error) {
    alert('게시글 수정에 실패했습니다.');
  }
};

onMounted(fetchPost);
</script> 