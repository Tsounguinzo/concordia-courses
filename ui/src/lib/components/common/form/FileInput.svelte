<script lang="ts">
    import {twMerge} from "tailwind-merge";

    export let file: File | null = null;
    export let id: string;
    export let name: string;
    export let accept: string;
    export let onChange: (file: File) => void;

    const handleChange = (e: Event) => {
        const target = e.target as HTMLInputElement;
        const file = target.files ? target.files[0] : null;
        if (file) {
            onChange(file);
        }
    };
</script>

<div class={twMerge('relative', $$props.class)}>
    <input
            id={id}
            name={name}
            accept={accept}
            type='file'
            on:change={handleChange}
            class='absolute inset-0 z-10 opacity-0 w-full h-full cursor-pointer'
    />
    {#if !file}
        <div class="flex flex-col items-center justify-center p-3">
            <svg
                    class="h-7 w-7"
                    xmlns="http://www.w3.org/2000/svg"
                    width="24"
                    height="24"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
            >
                <path d="M4 14.899A7 7 0 1 1 15.71 8h1.79a4.5 4.5 0 0 1 2.5 8.242"/>
                <path d="M12 12v9"/>
                <path d="m16 16-4-4-4 4"/>
            </svg>
            <p class="mt-2 text-center text-sm ">Click to upload.</p>
            <p class="mt-2 text-center text-sm ">Max file size: 4MB</p>
            <span class="sr-only">File upload</span>
        </div>
    {:else}
        <p class="p-3 border rounded-md dark:border-neutral-600 ">{file.name}</p>
    {/if}
</div>