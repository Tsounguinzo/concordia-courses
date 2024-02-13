<script lang="ts">
    import {Search} from 'lucide-svelte';
    import {twMerge} from 'tailwind-merge';
    import type {Writable} from "svelte/store";
    import {writable} from "svelte/store";

    export let handleInputChange: (value: string) => void;
    export let iconStyle: string = '';
    export let inputStyle: string = '';
    export let outerIconStyle: string = '';
    export let outerInputStyle: string = '';
    export let placeholder: string = '';
    export let value: string = '';
    export let searchSelected: Writable<boolean> = writable<boolean>(false);

</script>

<div class='relative w-full'>
    <div class={twMerge('pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3', outerIconStyle)}>
        <Search size={20} aria-hidden='true' class={twMerge(
            'transition duration-200',
            $searchSelected ? 'stroke-blue-500' : 'stroke-gray-400',
            iconStyle
          )}/>
    </div>
    <div class={outerInputStyle}>
        <input
                class={inputStyle}
                on:keydown
                on:blur={() => setTimeout(() => searchSelected.set(false), 100)}
                on:input={(event) => handleInputChange(event.target?.value)}
                on:focus={() => searchSelected.set(true)}
                placeholder={placeholder}
                spellCheck='false'
                type='text'
                bind:value={value}
        />
    </div>
</div>
