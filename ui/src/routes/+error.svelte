<script>
    import {page} from "$app/stores";
</script>

<div class="flex min-h-[calc(100vh-96px)] flex-col items-center justify-center">
    <div class='space-y-10 text-center justify-center'>
        <div class="flex items-center max-w-[32rem] my-1">
            <span class="text-5xl mr-1 font-bold text-gray-900 dark:text-gray-200">{$page.status}</span>
            <div class="text-3xl border-l-2 pl-2 ml-1 border-black dark:border-gray-200 border-solid font-medium text-gray-800 dark:text-gray-300">
                <h1>It looks like you got lost.</h1>
            </div>
        </div>
        <a href="/" class='block mx-auto max-w-fit rounded-full border px-5 py-2 font-semibold text-gray-800 transition duration-300 ease-in-out hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-neutral-800'>
            Home
        </a>
    </div>
</div>
