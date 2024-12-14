<script lang="ts">
    export let enrollmentCapacity: number;
    export let currentEnrollment: number;
    export let waitlistCapacity: number;
    export let currentWaitlistTotal: number;
    export let hasSeatReserved: string;

    $: isFull = currentEnrollment >= enrollmentCapacity;
    $: hasWaitlistSpace = waitlistCapacity > currentWaitlistTotal;
    
    $: status = isFull 
        ? (hasWaitlistSpace ? 'waitlist' : 'closed')
        : 'open';
    
    $: statusText = {
        'open': 'Open',
        'waitlist': 'Waitlist',
        'closed': 'Full'
    }[status];
</script>

<div class="flex flex-col sm:flex-row items-start sm:items-center gap-1 sm:gap-2">
    <!-- Status Badge -->
    <div class="px-2 py-0.5 rounded text-xs font-medium"
         class:bg-green-100={status === 'open'}
         class:text-green-700={status === 'open'}
         class:bg-yellow-100={status === 'waitlist'}
         class:text-yellow-700={status === 'waitlist'}
         class:bg-red-100={status === 'closed'}
         class:text-red-700={status === 'closed'}>
        {statusText}
    </div>

    <!-- Enrollment Numbers -->
    <div class="text-xs sm:text-sm">
        <span class="font-medium">{currentEnrollment}</span>
        <span class="text-gray-500">/</span>
        <span class="text-gray-500">{enrollmentCapacity}</span>

        {#if waitlistCapacity > 0}
            <span class="ml-2 text-gray-400 max-md:hidden">
                Waitlist: {currentWaitlistTotal}/{waitlistCapacity}
            </span>
        {/if}
    </div>
</div> 