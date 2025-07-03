<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    import type { Comment } from '$lib/model/Comment';
    import { format } from 'date-fns';
    import DeleteButton from './DeleteButton.svelte';
    import { Edit, Check, X } from 'lucide-svelte';
    
    export let comment: Comment;
    export let canDelete: boolean = false;
    export let canEdit: boolean = false;
    export let isDeleting: boolean = false;
    export let isUpdating: boolean = false;
    
    const dispatch = createEventDispatcher();
    
    let isEditing = false;
    let editedContent = '';
    
    function formatTimestamp(timestamp: string): string {
        return format(new Date(timestamp), 'MMM d, yyyy h:mm a');
    }
    
    function handleDelete() {
        dispatch('delete', comment._id);
    }
    
    function startEdit() {
        isEditing = true;
        editedContent = comment.content;
    }
    
    function cancelEdit() {
        isEditing = false;
        editedContent = '';
    }
    
    function saveEdit() {
        if (editedContent.trim() && editedContent.trim() !== comment.content) {
            dispatch('update', { commentId: comment._id, content: editedContent.trim() });
        }
        isEditing = false;
        editedContent = '';
    }
    
    function handleKeyDown(event: KeyboardEvent) {
        if (event.key === 'Escape') {
            cancelEdit();
        } else if (event.key === 'Enter' && (event.ctrlKey || event.metaKey)) {
            saveEdit();
        }
    }
</script>

<div class="group p-4 rounded-lg border border-gray-200 dark:border-neutral-600 
           bg-white dark:bg-neutral-700 
           transition-all duration-200 ease-in-out
           hover:border-gray-300 dark:hover:border-neutral-500 
           hover:shadow-sm">
    
    <!-- Comment Content -->
    <div class="space-y-3">
        <!-- Comment Text -->
        {#if isEditing}
            <div class="space-y-3">
                <textarea
                    bind:value={editedContent}
                    on:keydown={handleKeyDown}
                    class="w-full min-h-[80px] p-3 text-sm border border-gray-300 dark:border-neutral-600 rounded-lg 
                           bg-white dark:bg-neutral-700 text-gray-800 dark:text-gray-200
                           focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent
                           resize-y"
                    placeholder="Edit your comment..."
                    disabled={isUpdating}
                />
                <div class="flex items-center gap-2">
                    <button
                        on:click={saveEdit}
                        disabled={isUpdating || !editedContent.trim() || editedContent.trim() === comment.content}
                        class="inline-flex items-center gap-2 px-3 py-1.5 text-xs font-medium text-white bg-primary-600 rounded-lg hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 dark:focus:ring-offset-gray-800">
                        {#if isUpdating}
                            <svg class="w-3 h-3 animate-spin" fill="none" viewBox="0 0 24 24">
                                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                                <path class="opacity-75" fill="currentColor" d="m4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                            </svg>
                            Saving...
                        {:else}
                            <Check class="w-3 h-3" />
                            Save
                        {/if}
                    </button>
                    <button
                        on:click={cancelEdit}
                        disabled={isUpdating}
                        class="inline-flex items-center gap-2 px-3 py-1.5 text-xs font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-neutral-600 rounded-lg hover:bg-gray-200 dark:hover:bg-neutral-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200">
                        <X class="w-3 h-3" />
                        Cancel
                    </button>
                    <div class="text-xs text-gray-500 dark:text-gray-400 ml-auto">
                        Ctrl+Enter to save, Esc to cancel
                    </div>
                </div>
            </div>
        {:else}
            <div class="prose prose-sm max-w-none dark:prose-invert">
                <p class="text-gray-800 dark:text-gray-200 leading-relaxed m-0">
                    {@html comment.content.replace(/\n/g, '<br>')}
                </p>
            </div>
        {/if}
        
        <!-- Footer with timestamp and actions -->
        <div class="flex items-center justify-between pt-2 border-t border-gray-100 dark:border-neutral-500">
            <div class="text-xs text-gray-500 dark:text-gray-400">
                {#if comment.timestamp}
                    {formatTimestamp(comment.timestamp)}
                {/if}
            </div>
            
            {#if canEdit || canDelete}
                <div class="flex items-center gap-2">
                    {#if isDeleting}
                        <div class="flex items-center gap-2 text-xs text-gray-500 dark:text-gray-400">
                            <svg class="w-3 h-3 animate-spin" fill="none" viewBox="0 0 24 24">
                                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                                <path class="opacity-75" fill="currentColor" d="m4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                            </svg>
                            Deleting...
                        </div>
                    {:else}
                        {#if canEdit && !isEditing}
                            <button
                                on:click={startEdit}
                                disabled={isUpdating}
                                title="Edit comment">
                                <Edit class='cursor-pointer stroke-gray-500 transition duration-200 hover:stroke-gray-800 dark:stroke-gray-400 dark:hover:stroke-gray-200'
                                      size={16}/>
                            </button>
                        {/if}
                        {#if canDelete}
                            <DeleteButton
                                title="Delete Comment"
                                text="Are you sure you want to delete this comment? This action cannot be undone."
                                onConfirm={handleDelete}
                                size={16}/>
                        {/if}
                    {/if}
                </div>
            {/if}
        </div>
    </div>
</div> 