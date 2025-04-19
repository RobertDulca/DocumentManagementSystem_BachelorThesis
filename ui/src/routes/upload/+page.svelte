<script>
    import RestClient from '../../RestClient'; // Update with actual path if different
    import { onMount } from 'svelte';
  
    let documentTitle = '';
    let file;
    let message = '';
  
    async function handleUpload() {
      const formData = new FormData();
      formData.append('document', documentTitle);
      formData.append('file', file);
  
      try {
        const response = await RestClient.post('/api/documents/post_document', formData);
        message = '<div class="alert alert-success">Document successfully uploaded!</div>';
      } catch (error) {
        message = '<div class="alert alert-danger">Document upload failed due to bad request!</div>';
      }
    }
  </script>

  
  <div class="container mt-4">
    <h1 class="text-center">Upload Document</h1>
    <hr>
  
    <form on:submit|preventDefault={handleUpload}>
      <div class="form-group">
        <label for="documentTitle">Document Title</label>
        <input type="text" class="form-control" bind:value={documentTitle} required />
      </div>
      <div class="form-group">
        <label for="fileInput">Select File</label>
        <input type="file" class="form-control-file" on:change="{e => file = e.target.files[0]}" required />
      </div>
      <button type="submit" class="btn btn-primary">Upload Document</button>
    </form>
    {@html message}
  </div>
  