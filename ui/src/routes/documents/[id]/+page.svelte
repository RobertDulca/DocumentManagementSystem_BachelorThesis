<script>
    import { page } from '$app/stores'; // For accessing route params
    import { onMount } from 'svelte';
    import RestClient from '../../../RestClient'; // Adjust the path if needed

    let document = null;
    let error = null;

    onMount(async () => {
        try {
            const id = $page.params.id;
            document = await RestClient.get(`/api/documents/${id}`);
        } catch (err) {
            error = 'Error fetching document details';
            console.error(error, err);
        }
    });
</script>

<div class="container mt-4">
    {#if error}
        <div class="alert alert-danger">{error}</div>
    {:else if document}
        <h1 class="text-center">{document.title}</h1>
        <p><strong>Uploaded on:</strong> {new Date(document.created).toLocaleString()}</p>
        <hr>
        <pre class="border p-3 bg-light">{document.content}</pre>
        <a href="/documents" class="btn btn-secondary mt-3">Back to Documents</a>
    {:else}
        <div class="text-center">
            <div class="spinner-border" role="status">
                <span class="sr-only">Loading...</span>
            </div>
            <p>Loading document...</p>
        </div>
    {/if}
</div>
