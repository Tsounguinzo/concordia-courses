<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    
    export let value: string = '';
    export let placeholder: string = 'Share your thoughts on this review...';
    export let disabled: boolean = false;
    export let maxLength: number = 500;
    export let isSubmitting: boolean = false;
    
    const dispatch = createEventDispatcher();
    
    let textareaElement: HTMLTextAreaElement;
    
    function handleSubmit() {
        if (value.trim()) {
            dispatch('submit');
        }
    }
    
    function handleCancel() {
        value = '';
        dispatch('cancel');
    }
    
    function handleKeydown(event: KeyboardEvent) {
        // Submit on Ctrl/Cmd + Enter
        if ((event.ctrlKey || event.metaKey) && event.key === 'Enter') {
            event.preventDefault();
            handleSubmit();
        }
    }
    
    // Auto-resize textarea
    function autoResize() {
        if (textareaElement) {
            textareaElement.style.height = 'auto';
            textareaElement.style.height = textareaElement.scrollHeight + 'px';
        }
    }
    
    $: if (value) autoResize();
    $: charactersRemaining = maxLength - value.length;
    $: isOverLimit = charactersRemaining < 0;
</script>

<div class="space-y-3">
    <!-- Simple textarea -->
    <div class="relative">
        <textarea
            bind:this={textareaElement}
            bind:value={value}
            {placeholder}
            disabled={disabled || isSubmitting}
            maxlength={maxLength}
            rows="3"
            on:keydown={handleKeydown}
            on:input={autoResize}
            class="w-full p-3 border border-gray-300 dark:border-neutral-600 rounded-lg resize-none
                   text-gray-900 dark:text-gray-200 text-sm leading-relaxed
                   placeholder-gray-500 dark:placeholder-gray-400
                   bg-white dark:bg-neutral-700
                   focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent
                   disabled:opacity-50 disabled:cursor-not-allowed"
            style="min-height: 80px;" />
        
        <!-- Character counter -->
        <div class="absolute bottom-2 right-2 text-xs
                   {isOverLimit ? 'text-red-500' : 'text-gray-400'}">
            {charactersRemaining}
        </div>
    </div>
    
    <!-- Action buttons -->
    <div class="flex justify-end gap-2">
        <button 
            type="button"
            on:click={handleCancel}
            class="px-4 py-2 text-sm font-medium text-gray-600 dark:text-gray-400 
                   hover:text-gray-800 dark:hover:text-gray-200 
                   transition-colors duration-150">
            Cancel
        </button>
        
        <button 
            type="button"
            on:click={handleSubmit}
            disabled={disabled || isSubmitting || !value.trim() || isOverLimit}
            class="px-4 py-2 bg-primary-600 text-white text-sm font-medium rounded-lg
                   transition-all duration-200 ease-in-out
                   hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 dark:focus:ring-offset-gray-800
                   disabled:opacity-50 disabled:cursor-not-allowed
                   flex items-center gap-2">
            {#if isSubmitting}
                <svg class="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="m4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Posting...
            {:else}
                Comment
            {/if}
        </button>
    </div>
</div> 