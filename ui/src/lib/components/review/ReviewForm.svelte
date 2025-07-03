<script lang="ts">
    import type {Course} from "$lib/model/Course";
    import {Field} from "sveltik/src";
    import FieldLabel from "$lib/components/common/form/FieldLabel.svelte";
    import FieldError from "$lib/components/common/form/FieldError.svelte";
    import IconRatingInput from "./IconRatingInput.svelte";
    import {type Instructor} from "$lib/model/Instructor";
    import TagsInput from "$lib/components/review/TagsInput.svelte";
    import AutocompleteInput from "$lib/components/review/AutocompleteInput.svelte";
    import Submit from "$lib/components/common/form/Submit.svelte";

    export let props;
    export let course: Course | null = null;
    export let instructor: Instructor | null = null;
    export let setFieldValue: (name: string, value: any) => void;
    export let resetButton: boolean = false;
    export let variant: 'course' | 'instructor' | 'school' = 'course';
    export let isSubmitting: boolean;

    // State for new resource link inputs
    let newLinkUrl: string = '';
    let newLinkDescription: string = '';

    function addResourceLink() {
        if (!newLinkUrl.trim()) return; // Basic validation: URL cannot be empty
        const newLink = { url: newLinkUrl.trim(), description: newLinkDescription.trim() };
        const updatedResourceLinks = [...(props.values.resourceLinks || []), newLink];
        setFieldValue('resourceLinks', updatedResourceLinks);
        newLinkUrl = '';
        newLinkDescription = '';
    }

    function removeResourceLink(index: number) {
        const updatedResourceLinks = [...(props.values.resourceLinks || [])];
        updatedResourceLinks.splice(index, 1);
        setFieldValue('resourceLinks', updatedResourceLinks);
    }

</script>
<div style="max-width: calc(100vw - 10rem); max-height: calc(100vh - 10rem);" class='overflow-auto scrollbar-hide'>
    <div class='flex flex-col'>
        {#if variant === 'instructor'}
            <FieldLabel For='course'>Course Name</FieldLabel>
            <AutocompleteInput
                    {setFieldValue}
                    value={props.values.course}
                    options={instructor?.courses.map(c => `${c.subject} ${c.catalog}`) ?? []}
                    name='course'
                    placeholder='What course did you take...'
                    class='capitalize resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white w-full'
            />
            <FieldError name='course'/>
        {:else if variant === 'course'}
            <FieldLabel For='instructor'>Instructor Name</FieldLabel>
            <AutocompleteInput
                    {setFieldValue}
                    value={props.values.instructor}
                    options={course?.instructors ?? []}
                    name='instructor'
                    placeholder='Who was your prof...'
                    class='capitalize resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white w-full'
            />
            <FieldError name='instructor'/>
        {:else if variant === 'school'}
            <FieldLabel For='school'>School Name</FieldLabel>
            <Field
                    disabled={true}
                    on:input={(e) => props.values.school = e.target.value}
                    on:blur={props.handleBlur}
                    value="Concordia University - Montreal"
                    id='school'
                    name='school'
                    placeholder='What school did you attend...'
                    class='capitalize resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white'
            />
            <FieldError name='school'/>
        {/if}
    </div>
    <div class='flex gap-x-10 flex-wrap'>
        {#if variant === 'course' || variant === 'instructor' }
            <div class='flex flex-col gap-y-1'>
                <FieldLabel
                        For={variant === 'course' ? 'experience' : 'rating'}>{variant === 'course' ? 'Experience' : 'Clarity Rating'}</FieldLabel>
                <IconRatingInput
                        {setFieldValue}
                        on:blur={props.handleBlur}
                        rating={variant === 'course' ? props.values.experience : props.values.rating}
                        name={variant === 'course' ? 'experience' : 'rating'}
                        icon='star'
                />
                <FieldError name={variant === 'course' ? 'experience' : 'rating'}/>
            </div>
            <div class='flex flex-col gap-y-0.5'>
                <FieldLabel For='difficulty'>Difficulty</FieldLabel>
                <IconRatingInput
                        {setFieldValue}
                        on:blur={props.handleBlur}
                        rating={props.values.difficulty}
                        name='difficulty'
                        icon='flame'
                />
                <FieldError name='difficulty'/>
            </div>
        {/if}
    </div>
    <div class='py-1'/>
    <div class='flex flex-col'>
        <FieldLabel For='content'>Content</FieldLabel>
        <Field
                on:input={(e) => props.values.content = e.target.value}
                on:blur={props.handleBlur}
                value={props.values.content}
                as='textarea'
                rows="8"
                id='content'
                name='content'
                placeholder='Write your thoughts on this course...'
                class='resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white'
        />
        <FieldError name='content'/>
    </div>

    <!-- Resource Links Section -->
    <div class='py-4'>
        <div class="flex items-center gap-2 mb-4">
            <FieldLabel For='content'>Useful Resource (Optional)</FieldLabel>
        </div>
        
        <p class="text-sm text-gray-600 dark:text-gray-400 mb-4">
            Share helpful links like study guides, practice exams, course websites, or other resources that helped you succeed.
        </p>

        <!-- Existing Resource Links -->
        {#if props.values.resourceLinks && props.values.resourceLinks.length > 0}
            <div class="space-y-3 mb-4">
                {#each props.values.resourceLinks as link, i}
                    <div class="group relative overflow-hidden rounded-lg border border-gray-200 dark:border-neutral-600 bg-white dark:bg-neutral-700 p-4 transition-all duration-200 hover:border-primary-300 dark:hover:border-primary-400 hover:shadow-md">
                        <div class="flex items-center gap-3">
                                <div class="flex-shrink-0 mt-0.5">
                                    <div class="w-10 h-10 rounded-full bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center group-hover:bg-primary-200 dark:group-hover:bg-primary-800/40 transition-colors duration-200">
                                        <svg class="w-5 h-5 text-primary-600 dark:text-primary-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14"/>
                                        </svg>
                                    </div>
                                </div>
                                <div class="flex-grow min-w-0">
                                    {#if link.description}
                                    <h5 class="text-sm font-medium text-gray-900 dark:text-gray-100 mb-1">
                                        {link.description}
                                    </h5>
                                    {/if}
                                    <a href={link.url} target="_blank" rel="noopener noreferrer"
                                       class="text-sm text-primary-600 dark:text-primary-400 hover:text-primary-700 dark:hover:text-primary-300 break-all transition-colors duration-150 underline decoration-transparent hover:decoration-current underline-offset-2">
                                        {link.url}
                                    </a>
                                </div>
                                <div class="flex-shrink-0 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                                    <button
                                        type="button"
                                        on:click={() => removeResourceLink(i)}
                                        class="p-2 text-gray-400 hover:text-red-500 dark:hover:text-red-400 transition-colors duration-150 rounded-lg hover:bg-red-50 dark:hover:bg-red-900/20"
                                        title="Remove resource">
                                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                                        </svg>
                                    </button>
                                </div>
                            </div>
                    </div>
                {/each}
            </div>
        {/if}

        <!-- Add New Resource Form -->
        <div class="border-2 border-dashed border-gray-300 dark:border-neutral-600 rounded-xl p-6 transition-all duration-200 hover:border-primary-300 dark:hover:border-primary-400 hover:bg-gray-50 dark:hover:bg-neutral-800/50">
            <div class="text-center mb-4">
                <div class="w-12 h-12 mx-auto mb-3 rounded-full bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center">
                    <svg class="w-6 h-6 text-primary-600 dark:text-primary-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
                    </svg>
                </div>
                <h5 class="text-sm font-medium text-gray-900 dark:text-gray-100 mb-1">Add a helpful resource</h5>
                <p class="text-xs text-gray-500 dark:text-gray-400">Share links that made a difference in your learning</p>
            </div>

            <div class="space-y-4">
                <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                        Resource URL *
                    </label>
                    <div class="relative">
                        <input 
                            type="url" 
                            placeholder="https://example.com/study-guide" 
                            bind:value={newLinkUrl} 
                            class="w-full pl-10 pr-4 py-3 border border-gray-300 dark:border-neutral-600 rounded-lg 
                                   bg-white dark:bg-neutral-700 text-gray-900 dark:text-gray-200 
                                   placeholder-gray-500 dark:placeholder-gray-400 text-sm
                                   transition-all duration-200 ease-in-out
                                   focus:border-primary-400 focus:ring-2 focus:ring-primary-100 dark:focus:ring-primary-900/20
                                   hover:border-gray-400 dark:hover:border-neutral-500" />
                        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                            <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101m-.758-4.899a4 4 0 005.656 0l4-4a4 4 0 00-5.656-5.656l-1.1 1.1"/>
                            </svg>
                        </div>
                    </div>
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                        Description
                    </label>
                    <div class="relative">
                        <input 
                            type="text" 
                            placeholder="e.g., 'Comprehensive study guide with practice problems'" 
                            bind:value={newLinkDescription} 
                            class="w-full pl-10 pr-4 py-3 border border-gray-300 dark:border-neutral-600 rounded-lg 
                                   bg-white dark:bg-neutral-700 text-gray-900 dark:text-gray-200 
                                   placeholder-gray-500 dark:placeholder-gray-400 text-sm
                                   transition-all duration-200 ease-in-out
                                   focus:border-primary-400 focus:ring-2 focus:ring-primary-100 dark:focus:ring-primary-900/20
                                   hover:border-gray-400 dark:hover:border-neutral-500" />
                        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                            <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z"/>
                            </svg>
                        </div>
                    </div>
                </div>

                <div class="flex justify-end">
                    <button 
                        type="button" 
                        on:click={addResourceLink} 
                        disabled={!newLinkUrl.trim()}
                        class="group relative overflow-hidden px-6 py-3 bg-primary-600 text-white text-sm font-medium rounded-lg
                               transition-all duration-200 ease-in-out
                               hover:bg-primary-700 hover:shadow-lg hover:shadow-primary-200 dark:hover:shadow-primary-900/30
                               focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 dark:focus:ring-offset-gray-800
                               disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-primary-600 disabled:hover:shadow-none
                               active:scale-95">
                        <span class="relative z-10 flex items-center gap-2">
                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
                            </svg>
                            Add Resource
                        </span>
                        <div class="absolute inset-0 bg-gradient-to-r from-primary-600 to-primary-500 opacity-0 group-hover:opacity-100 transition-opacity duration-200"></div>
                    </button>
                </div>
            </div>
        </div>

        <!-- Helper Text -->
        <div class="mt-4 p-3 bg-blue-50 dark:bg-blue-900/20 rounded-lg border border-blue-200 dark:border-blue-800">
            <div class="flex items-start gap-2">
                <svg class="w-4 h-4 text-blue-600 dark:text-blue-400 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
                <div class="text-sm text-blue-800 dark:text-blue-200">
                    <p class="font-medium mb-1">💡 Great resources to share:</p>
                    <ul class="text-xs space-y-1 list-disc list-inside ml-4">
                        <li>Professor's course website or syllabus</li>
                        <li>Textbook companion sites with practice problems</li>
                        <li>Online tutorials or video series</li>
                        <li>Study guides or cheat sheets you found helpful</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
    <!-- End of Resource Links Section -->

    <div class='flex flex-col'>
        {#if variant === 'instructor'}
            <FieldLabel For='tags'>Tags (0 min. - 3 max.)</FieldLabel>
            <TagsInput
                    {setFieldValue}
                    selectedTags={props.values.tags}
                    limit={3}
                    name='tags'
            />
            <FieldError name='tags'/>
        {/if}
        <div class='mt-8 flex justify-end space-x-4'>
            <button type="reset"
                    class='w-fit cursor-pointer rounded-md bg-gray-100 px-4 py-2 font-medium text-gray-700 duration-200 hover:bg-gray-200 dark:bg-neutral-700 dark:text-gray-200 dark:hover:bg-neutral-600'
            >
                {resetButton ? "Reset" : "Discard"}
            </button>
            <Submit {isSubmitting}/>
        </div>
    </div>
</div>