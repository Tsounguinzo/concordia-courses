<script lang="ts">
    import FillBar from "./FillBar.svelte";
    import {Flame, Star} from "lucide-svelte";

    export let title: string;
    export let value: number = 0;
    export let icon: 'flame' | 'star';
    export let variant: 'small' | 'medium' | 'large' = 'medium';
</script>

<div class='flex items-center gap-x-1'>
    {#if icon === 'flame' }
        <Flame class='-mt-0.5 stroke-primary-600' size={18}/>
    {:else }
        <Star class='-mt-0.5 stroke-primary-600' size={18}/>
    {/if}
    <div class='mb-0.5 text-xs font-medium uppercase tracking-wider text-gray-600 dark:text-gray-400'>
        {title}
    </div>
</div>
<div class='py-0.5'/>
<div class='flex items-center gap-x-2'>
    <FillBar width={variant === 'small' ? 144 : 180}
             percentage={value * 20}
             text={`${value}/5`}
             variant={variant}/>
</div>