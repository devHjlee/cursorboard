<template>
  <div class="bg-white shadow sm:rounded-lg">
    <div class="px-4 py-5 sm:p-6">
      <div class="flex justify-between items-center mb-6">
        <h3 class="text-2xl font-bold text-gray-900">{{ post?.title }}</h3>
        <div class="flex space-x-2">
          <button
            v-if="canEdit"
            @click="editPost"
            class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
          >
            수정
          </button>
          <button
            v-if="canEdit"
            @click="deletePost"
            class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-red-600 hover:bg-red-700"
          >
            삭제
          </button>
        </div>
      </div>

      <div class="prose max-w-none">
        <p class="text-gray-500 mb-4">
          작성자: {{ post?.authorEmail }} | 작성일: {{ formatDate(post?.createdAt) }}
        </p>
        <p class="whitespace-pre-wrap">{{ post?.content }}</p>
      </div>

      <div class="mt-8">
        <h4 class="text-lg font-medium text-gray-900 mb-4">댓글</h4>
        <div class="space-y-4">
          <div v-for="comment in post?.comments" :key="comment.id" class="border rounded-lg p-4">
            <div class="flex justify-between items-start">
              <div>
                <p class="text-sm text-gray-500">
                  {{ comment.authorEmail }} | {{ formatDate(comment.createdAt) }}
                </p>
                <p class="mt-1">{{ comment.content }}</p>
              </div>
              <button
                v-if="canDeleteComment(comment)"
                @click="deleteComment(comment.id)"
                class="text-sm text-red-600 hover:text-red-900"
              >
                삭제
              </button>
            </div>
          </div>
        </div>

        <div class="mt-6">
          <textarea
            v-model="newComment"
            rows="3"
            class="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-md"
            placeholder="댓글을 작성하세요"
          ></textarea>
          <div class="mt-3 flex justify-end">
            <button
              @click="submitComment"
              class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
            >
              댓글 작성
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import type { Post, Comment } from '@/types';
import api from '@/api/axios';
import { useAuthStore } from '@/store/auth';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const post = ref<Post | null>(null);
const newComment = ref('');

const canEdit = computed(() => {
  if (!post.value || !authStore.user) return false;
  return post.value.authorEmail === authStore.user.email || authStore.user.role === 'ADMIN';
});

const fetchPost = async () => {
  try {
    const response = await api.get(`/posts/${route.params.id}`);
    post.value = response.data;
  } catch (error) {
    alert('게시글을 불러오는데 실패했습니다.');
    router.push('/posts');
  }
};

const editPost = () => {
  router.push(`/posts/${post.value?.id}/edit`);
};

const deletePost = async () => {
  if (!confirm('정말 삭제하시겠습니까?')) return;

  try {
    await api.delete(`/posts/${post.value?.id}`);
    router.push('/posts');
  } catch (error) {
    alert('게시글 삭제에 실패했습니다.');
  }
};

const canDeleteComment = (comment: Comment) => {
  if (!authStore.user) return false;
  return comment.authorEmail === authStore.user.email || authStore.user.role === 'ADMIN';
};

const deleteComment = async (commentId: number) => {
  if (!confirm('댓글을 삭제하시겠습니까?')) return;

  try {
    await api.delete(`/posts/comments/${commentId}`);
    await fetchPost();
  } catch (error) {
    alert('댓글 삭제에 실패했습니다.');
  }
};

const submitComment = async () => {
  if (!newComment.value.trim()) {
    alert('댓글 내용을 입력해주세요.');
    return;
  }

  try {
    await api.post(`/posts/${post.value?.id}/comments`, { content: newComment.value });
    newComment.value = '';
    await fetchPost();
  } catch (error) {
    alert('댓글 작성에 실패했습니다.');
  }
};

const formatDate = (dateString?: string) => {
  if (!dateString) return '';
  return new Date(dateString).toLocaleDateString('ko-KR');
};

onMounted(fetchPost);
</script> 