import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';

export default {
    plugins: [sveltekit()],
    server: {
        proxy: {
            '/api': 'http://localhost:8081'
        }
    }
};
