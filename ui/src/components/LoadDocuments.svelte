<script>
    import client from '../RestClient.js';

    let documents = [];
    let error = null;

    async function loadDocuments() {
        try {
            error = null;
            documents = await client.get('/api/documents');
        } catch (err) {
            error = 'Could not load documents. Please try again.';
            console.error(err);
        }
    }
</script>

<button on:click={loadDocuments}>Load Documents</button>

{#if error}
    <p style="color: red;">{error}</p>
{/if}

{#if documents.length > 0}
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Author</th>
                <th>Created</th>
            </tr>
        </thead>
        <tbody>
            {#each documents as document}
                <tr>
                    <td>{document.id}</td>
                    <td>{document.title}</td>
                    <td>{document.author}</td>
                    <td>{document.created ? document.created : 'N/A'}</td>
                </tr>
            {/each}
        </tbody>
    </table>
{/if}
