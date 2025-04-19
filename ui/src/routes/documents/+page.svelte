<script>
  import RestClient from '../../RestClient'; // Update with actual path if different
  import { onMount } from 'svelte';

  let documents = [];
  let searchQuery = ''; // Variable to hold the search query

  // Fetch documents on page load
  onMount(async () => {
    await fetchDocuments();
  });

  // Function to fetch documents with optional search query
  async function fetchDocuments(query = '') {
    try {
      const searchParam = query ? `?search=${encodeURIComponent(query)}` : '';
      documents = await RestClient.get(`/api/documents${searchParam}`);
    } catch (error) {
      console.error('Error fetching documents:', error);
    }
  }

  // Function to handle search button click
  async function handleSearch() {
    await fetchDocuments(searchQuery);
  }

  // Function to delete a document
  async function deleteDocument(id) {
    try {
      const response = await RestClient.delete(`/api/documents/${id}?id=${id}`);
      if (response.ok) {
        // Remove the deleted document from the list
        await fetchDocuments(searchQuery); // Fetch documents again to reflect current search results
      } else {
        console.error('Error deleting document:', response.statusText);
      }
    } catch (error) {
      console.error('Error deleting document:', error);
    }
  }
</script>

<div class="container mt-4">
  <h1 class="text-center">Documents</h1>
  <hr>

  <!-- Search bar -->
  <div class="d-flex mb-3">
    <input
            type="text"
            class="form-control me-2"
            placeholder="Search documents..."
            bind:value={searchQuery}
    />
    <button class="btn btn-primary" on:click={handleSearch}>Search</button>
  </div>

  <table class="table table-striped">
    <thead class="thead-dark">
    <tr>
      <th>#</th>
      <th>Document Name</th>
      <th>Date Uploaded</th>
      <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    {#each documents as document, index}
      <tr>
        <th>{index + 1}</th>
        <td>{document.title}</td>
        <td>{new Date(document.created).toLocaleDateString()}</td>
        <td>
          <a href={`/documents/${document.id}`} class="btn btn-primary btn-sm">View</a>
          <button
                  class="btn btn-danger btn-sm"
                  on:click={() => deleteDocument(document.id)}
          >
            Delete
          </button>
        </td>
      </tr>
    {/each}
    </tbody>
  </table>
</div>
