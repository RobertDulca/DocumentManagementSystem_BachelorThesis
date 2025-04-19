<!-- +layout.svelte -->
<script>
    import { onMount } from 'svelte';
 </script>
 
 <nav>
    <ul>
       <li><a href="/">Home</a></li>
       <li><a href="/documents">Documents</a></li>
       <li><a href="/upload">Upload</a></li>
    </ul>
 </nav>
 
 <main>
    <slot /> <!-- This will render the content of each route -->
 </main>
 
 <footer>
    <p>&copy; 2024 Your Company</p>
 </footer>
 
 <style>
    nav ul {
       display: flex;
       list-style: none;
       gap: 10px;
    }
 
    nav ul li {
       margin: 0 10px;
    }
 
    footer {
       margin-top: 20px;
       text-align: center;
    }
 </style>
 