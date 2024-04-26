<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import {FileText} from "lucide-svelte";

    export let className: string = "";
    export let isLogin: boolean = false;
    export let variant: 'course' | 'instructor' | 'school' = "course";
</script>

{#if isLogin}
    <div class="py-2"/>
{/if}

<div class={twMerge('text-gray-500 dark:text-gray-400 pb-56 flex justify-center', className)}>
    <div class='py-1'/>
    <div class='mx-4 flex items-center gap-x-2 text-center text-sm sm:text-base'>
        <FileText class='stroke-gray-400 stroke-[1px]' size={40}/>
        <div>
            No reviews have been left for this {variant} yet,
            {#if !isLogin}
                <a href="/login" class="underline text-blue-400">Login</a> to
            {/if}
            be the first!
        </div>
    </div>
</div>